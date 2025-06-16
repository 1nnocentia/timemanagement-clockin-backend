package com.clockin.clockin.controller;

import com.clockin.clockin.model.User;
import com.clockin.clockin.dto.LoginRequest;
import com.clockin.clockin.dto.LoginResponse;
import com.clockin.clockin.dto.ForgotPasswordRequest;
import com.clockin.clockin.dto.ResetPasswordRequest;
import com.clockin.clockin.dto.UserUpdateRequest;
import com.clockin.clockin.service.UserService;
import com.clockin.clockin.service.StreakService;
import com.clockin.clockin.service.NotificationService;
import com.clockin.clockin.model.Streak;
import com.clockin.clockin.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    // Daftar ID gambar profil yang tersedia. Frontend akan menggunakan ID ini.
    public static final List<String> AVAILABLE_PROFILE_PICTURE_IDS = List.of(
            "aset_aplikasi_personal_1", "aset_aplikasi_personal_2", "aset_aplikasi_personal_3", "aset_aplikasi_personal_4", "aset_aplikasi_personal_5", "aset_aplikasi_personal_6"
    );

    @Autowired
    private UserService userService;

    @Autowired
    private StreakService streakService;

    @Autowired
    private NotificationService notificationService;

    // Method: POST
    // http://localhost:8080/api/signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred during registration.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Method: POST
    // http://localhost:8080/api/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // Log untuk debugging: menampilkan username/email dan password yang diterima
        System.out.println("DEBUG: usernameOrEmail = " + loginRequest.getUsernameOrEmail());
        System.out.println("DEBUG: password = " + loginRequest.getPassword());

        // user service for authentication and token generation
        Optional<String> jwtTokenOptional = userService.authenticateAndGenerateToken(
            loginRequest.getUsernameOrEmail(),
            loginRequest.getPassword()
        );

        // token validation
        if (jwtTokenOptional.isPresent()) {
            String jwtToken = jwtTokenOptional.get();
            Optional<User> userOptional = userService.getUserByUsernameOrEmail(loginRequest.getUsernameOrEmail());

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return new ResponseEntity<>(new LoginResponse("Login Success!", true, jwtToken, user.getId()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new LoginResponse("An internal error occurred after login (user not found after authentication).", false, null, null), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            return new ResponseEntity<>(new LoginResponse("Username/Email or password not valid.", false, null, null), HttpStatus.UNAUTHORIZED);
        }
    }

    // Method: POST
    // localhost:8080/api/logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        System.out.println("User attempted logout. Frontend is expected to delete JWT token.");
        return new ResponseEntity<>("Logout successful. Your token is expected to have been deleted from the client side.", HttpStatus.OK);
    }

    // Method: POST
    // localhost:8080/api/forgot-password/request
    @PostMapping("/forgot-password/request")
    public ResponseEntity<String> forgotPasswordRequest(@RequestBody ForgotPasswordRequest request) {
        try {
            String token = userService.initiatePasswordReset(request.getEmail());
            return new ResponseEntity<>("A password reset token has been generated for the email. " + request.getEmail() + ". (Check the server console for the token. In production, this will be sent via email)", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("If your email is registered, password reset instructions have been sent. Error message: " + e.getMessage(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while processing the password reset request.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Method: POST
    // localhost:8080/api/forgot-password/reset
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request.getToken(), request.getNewPassword());
            return new ResponseEntity<>("Password successfully reset.", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while resetting the password.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Method: POST
    // URL: /api/users/{userId}/record-interaction
    @PostMapping("/users/{userId}/record-interaction")
    public ResponseEntity<?> recordUserInteraction(@PathVariable Long userId) {
        try {
            Streak updatedStreak = streakService.recordInteraction(userId);
            return new ResponseEntity<>(updatedStreak, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Terjadi kesalahan saat merekam interaksi: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Method: GET
    // localhost:8080/api/users/{userId}/streak
    @GetMapping("/users/{userId}/streak")
    public ResponseEntity<?> getUserStreak(@PathVariable Long userId) {
        try {
            Optional<Streak> streak = streakService.getStreakByUserId(userId);
            if (streak.isPresent()) {
                return new ResponseEntity<>(streak.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Streak does not exist for this user.", HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Method: GET
    // URL: /api/users/{userId}/notifications
    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        }
        catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Method: GET
    // localhost:8080/api/users/{userId}/notifications/unread
    @GetMapping("/users/{userId}/notifications/unread")
    public ResponseEntity<List<Notification>> getUserUnreadNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotificationsByUserId(userId);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Method: PUT
    // localhost:8080/api/notifications/{notificationId}/read
    @PutMapping("/notifications/{notificationId}/read")
    public ResponseEntity<HttpStatus> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            boolean success = notificationService.markNotificationAsRead(notificationId);
            if (success) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Method: GET
    // localhost:8080/api/users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Method: GET
    // localhost:8080/api/users/{id}
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        Optional<User> userData = userService.getUserById(id);
        return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Method: PUT
    // localhost:8080/api/users/{id}
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        try {
            User updatedUser = userService.updateUser(id, userUpdateRequest);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Method: DELETE
    // localhost:8080/api/users/{id}
    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}