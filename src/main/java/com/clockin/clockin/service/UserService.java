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

// Anotasi @Service menandakan kelas ini adalah komponen service Spring
@Service
public class UserService {

    // ** MODIFIKASI: Pastikan ini PRIVATE dan di-autowire. **
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // Batasan hari untuk perubahan username
    private static final int USERNAME_CHANGE_COOLDOWN_DAYS = 30;
    // Masa berlaku token reset password dalam menit
    private static final long RESET_TOKEN_VALIDITY_MINUTES = 15;

    // Metode untuk mendaftarkan pengguna baru (signup)
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
            user.setProfilePictureId("avatar-1"); // Set default avatar
        }
        return userRepository.save(user);
    }

    /**
     * Mengautentikasi pengguna dan mengembalikan token JWT jika berhasil.
     * @param usernameOrEmail Username atau email pengguna.
     * @param password Password pengguna.
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
            System.err.println("Autentikasi gagal untuk user: " + usernameOrEmail + ". Error: " + e.getMessage());
            return Optional.empty();
        }
    }

    // ** BARU: Metode untuk mendapatkan User berdasarkan username atau email **
    // Ini adalah metode yang harus dipanggil oleh UserController
    public Optional<User> getUserByUsernameOrEmail(String usernameOrEmail) {
        User user = userRepository.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail);
        }
        return Optional.ofNullable(user); // Mengembalikan Optional.empty() jika user null
    }

    // Metode untuk mendapatkan semua pengguna
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Metode untuk mendapatkan pengguna berdasarkan ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Metode untuk memperbarui detail pengguna.
     * Termasuk logika untuk batasan perubahan username dan hashing password.
     *
     * @param id The ID of the user to update.
     * @param userDetails User object with details to be updated.
     * @return The updated User object.
     * @throws RuntimeException if the user is not found or the username change cooldown is violated.
     */
    @Transactional
    public User updateUser(Long id, UserUpdateRequest userUpdateRequest) { // MODIFIKASI: Menerima UserUpdateRequest
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // MODIFIKASI: Hanya update 'nama' jika disediakan dalam request body dan tidak kosong
        if (userUpdateRequest.getNama() != null && !userUpdateRequest.getNama().isEmpty()) {
            existingUser.setNama(userUpdateRequest.getNama());
        }

        // MODIFIKASI: Hanya update 'username' jika disediakan dan berbeda dari yang ada
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

        // MODIFIKASI: Hanya update 'email' jika disediakan dan berbeda dari yang ada
        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().isEmpty() &&
            !existingUser.getEmail().equals(userUpdateRequest.getEmail())) {
            
            User userWithNewEmail = userRepository.findByEmail(userUpdateRequest.getEmail());
            if (userWithNewEmail != null && !userWithNewEmail.getId().equals(id)) {
                throw new RuntimeException("Email '" + userUpdateRequest.getEmail() + "' is already in use by another user.");
            }
            existingUser.setEmail(userUpdateRequest.getEmail());
        }

        // Logika update 'password' sudah memiliki null/empty check
        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }

        // Logika update 'profilePictureId' sudah memiliki null/empty check
        if (userUpdateRequest.getProfilePictureId() != null && !userUpdateRequest.getProfilePictureId().isEmpty()) {
            existingUser.setProfilePictureId(userUpdateRequest.getProfilePictureId());
        }

        return userRepository.save(existingUser);
    }


    // Metode untuk menghapus pengguna
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

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