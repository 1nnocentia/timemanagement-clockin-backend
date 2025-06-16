package com.clockin.clockin.service;

import com.clockin.clockin.model.User;
import com.clockin.clockin.repository.UserRepository;
import com.clockin.clockin.config.JwtUtil;
import com.clockin.clockin.dto.UserUpdateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // username change cooldown in days
    private static final int USERNAME_CHANGE_COOLDOWN_DAYS = 30;
    // reset password token validity in minutes
    private static final long RESET_TOKEN_VALIDITY_MINUTES = 15;

    // signup
    @Transactional
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username '" + user.getUsername() + "' sudah ada.");
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email '" + user.getEmail() + "' sudah ada.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setLastUsernameChangeDate(LocalDate.now(ZoneOffset.UTC));
        if (user.getProfilePictureId() == null || user.getProfilePictureId().isEmpty()) {
            user.setProfilePictureId("aset_aplikasi_personal_1"); // defaultnya 1
        }
        return userRepository.save(user);
    }

    /**
     * User authentication and JWT token generation.
     * @param usernameOrEmail
     * @param password
     * @return Optional<String> containing the JWT token if authentication is successful, or Optional.empty() if it fails.
     */
    public Optional<String> authenticateAndGenerateToken(String usernameOrEmail, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwtToken = jwtUtil.generateToken(userDetails);
            return Optional.of(jwtToken);
        } catch (Exception e) {
            System.err.println("Autentikasi failed for user_id: " + usernameOrEmail + ". Error: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Get user by username or email.
     * If True = return optional user
     * If False = return empty optional
     *
     * @param usernameOrEmail
     * @return Optional<User> or Optional.empty()
     */
    public Optional<User> getUserByUsernameOrEmail(String usernameOrEmail) {
        User user = userRepository.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail);
        }
        return Optional.ofNullable(user);
    }

    // get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // get user by id
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * update user details
     *
     * @param id - user_id
     * @param userDetails - UserUpdateRequest object containing the details to update
     * @return updated user object
     * @throws RuntimeException if the user is not found or the username change cooldown is violated
     */
    @Transactional
    public User updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // name update
        if (userUpdateRequest.getNama() != null && !userUpdateRequest.getNama().isEmpty()) {
            existingUser.setNama(userUpdateRequest.getNama());
        }

       // username update (cooldown check and unique check)
        if (userUpdateRequest.getUsername() != null && !userUpdateRequest.getUsername().isEmpty() &&
            !existingUser.getUsername().equals(userUpdateRequest.getUsername())) {
            
            LocalDate today = LocalDate.now(ZoneOffset.UTC);
            if (existingUser.getLastUsernameChangeDate() != null &&
                existingUser.getLastUsernameChangeDate().plusDays(USERNAME_CHANGE_COOLDOWN_DAYS).isAfter(today)) {
                throw new RuntimeException("You can only change your username every " + USERNAME_CHANGE_COOLDOWN_DAYS + " days.");
            }
            User userWithNewUsername = userRepository.findByUsername(userUpdateRequest.getUsername());
            if (userWithNewUsername != null && !userWithNewUsername.getId().equals(id)) {
                throw new RuntimeException("Username '" + userUpdateRequest.getUsername() + "' is already in use by another user.");
            }
            existingUser.setUsername(userUpdateRequest.getUsername());
            existingUser.setLastUsernameChangeDate(today);
        }

        // email update (unique check)
        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().isEmpty() &&
            !existingUser.getEmail().equals(userUpdateRequest.getEmail())) {
            
            User userWithNewEmail = userRepository.findByEmail(userUpdateRequest.getEmail());
            if (userWithNewEmail != null && !userWithNewEmail.getId().equals(id)) {
                throw new RuntimeException("Email '" + userUpdateRequest.getEmail() + "' is already in use by another user.");
            }
            existingUser.setEmail(userUpdateRequest.getEmail());
        }

        // password update
        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }

        // profile update
        if (userUpdateRequest.getProfilePictureId() != null && !userUpdateRequest.getProfilePictureId().isEmpty()) {
            existingUser.setProfilePictureId(userUpdateRequest.getProfilePictureId());
        }

        return userRepository.save(existingUser);
    }

    // delete user by id
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Initiate password reset by generating a token and setting its expiry date
     * @param email 
     * @return generated reset token
     * @throws RuntimeException if user with the given email is not found
     */
    @Transactional
    public String initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User with email '" + email + "' not found.");
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(RESET_TOKEN_VALIDITY_MINUTES);

        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiryDate(expiryDate);
        userRepository.save(user);

        System.out.println("Reset token for " + email + ": " + token + " (Valid until: " + expiryDate + ")");
        return token;
    }

    /**
     * Reset user password using the provided token and new password.
     * @param token - reset password token
     * @param newPassword - new password to set
     * @throws RuntimeException if the token is invalid or expired
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token);

        if (user == null) {
            throw new RuntimeException("Invalid password reset token.");
        }

        if (user.getResetPasswordTokenExpiryDate() == null ||
            user.getResetPasswordTokenExpiryDate().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            user.setResetPasswordToken(null);
            user.setResetPasswordTokenExpiryDate(null);
            userRepository.save(user);
            throw new RuntimeException("Password reset token has expired or is invalid. Please request a new token.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiryDate(null);
        userRepository.save(user);
    }
}