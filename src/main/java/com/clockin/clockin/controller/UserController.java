package com.clockin.clockin.controller;

import com.clockin.clockin.model.User;
import com.clockin.clockin.dto.LoginRequest;
import com.clockin.clockin.dto.LoginResponse;
import com.clockin.clockin.dto.ForgotPasswordRequest;
import com.clockin.clockin.dto.ResetPasswordRequest;
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

// Anotasi @RestController menandakan kelas ini adalah RESTful controller
@RestController
// Anotasi @RequestMapping untuk menentukan base path untuk semua endpoint di controller ini
@RequestMapping("/api")
public class UserController {

    public static final List<String> AVAILABLE_PROFILE_PICTURE_IDS = List.of(
            "avatar-1", "avatar-2", "avatar-3", "avatar-4", "avatar-5", "avatar-6"
    );

    @Autowired
    private UserService userService;

    @Autowired
    private StreakService streakService;

    @Autowired
    private NotificationService notificationService;

    // Endpoint untuk pendaftaran pengguna baru (signup)
    // POST /api/signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Terjadi kesalahan saat pendaftaran.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint untuk login pengguna dengan JWT
    // POST /api/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Optional<String> jwtTokenOptional = userService.authenticateAndGenerateToken(
            loginRequest.getUsernameOrEmail(),
            loginRequest.getPassword()
        );

        if (jwtTokenOptional.isPresent()) {
            String jwtToken = jwtTokenOptional.get();
            User user = null;
            String usernameOrEmail = loginRequest.getUsernameOrEmail();
            User userByUsername = userService.userRepository.findByUsername(usernameOrEmail);
            if (userByUsername != null) {
                user = userByUsername;
            } else {
                user = userService.userRepository.findByEmail(usernameOrEmail);
            }

            if (user != null) {
                return new ResponseEntity<>(new LoginResponse("Login Berhasil!", true, jwtToken, user.getId()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new LoginResponse("Terjadi kesalahan internal setelah login.", false, null, null), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            return new ResponseEntity<>(new LoginResponse("Username/Email atau password salah.", false, null, null), HttpStatus.UNAUTHORIZED);
        }
    }

    // Endpoint untuk logout pengguna
    // POST /api/logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Dalam sistem berbasis token (JWT), logout sebagian besar terjadi di sisi klien
        // dengan menghapus token yang disimpan (misalnya, dari Local Storage, Session Storage, atau cookies).
        // Backend tidak perlu "mengakhiri sesi" karena sesi tidak disimpan di server.
        System.out.println("Pengguna melakukan percobaan logout. Frontend diharapkan untuk menghapus token JWT.");
        return new ResponseEntity<>("Logout berhasil. Token Anda diharapkan telah dihapus dari sisi klien.", HttpStatus.OK);
    }

    // Endpoint untuk memulai permintaan lupa password
    // POST /api/forgot-password/request
    @PostMapping("/forgot-password/request")
    public ResponseEntity<String> forgotPasswordRequest(@RequestBody ForgotPasswordRequest request) {
        try {
            String token = userService.initiatePasswordReset(request.getEmail());
            return new ResponseEntity<>("Token reset password telah dihasilkan untuk email " + request.getEmail() + ". (Cek konsol server untuk token. Dalam produksi, ini akan dikirim via email)", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Jika email Anda terdaftar, instruksi reset password telah dikirim. Pesan error: " + e.getMessage(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Terjadi kesalahan saat memproses permintaan reset password.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint untuk mereset password menggunakan token
    // POST /api/forgot-password/reset
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request.getToken(), request.getNewPassword());
            return new ResponseEntity<>("Password berhasil direset.", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Terjadi kesalahan saat mereset password.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint untuk merekam interaksi pengguna dan memperbarui streak
    // POST /api/users/{userId}/record-interaction
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

    // Endpoint untuk mendapatkan streak pengguna
    // GET /api/users/{userId}/streak
    @GetMapping("/users/{userId}/streak")
    public ResponseEntity<?> getUserStreak(@PathVariable Long userId) {
        try {
            Optional<Streak> streak = streakService.getStreakByUserId(userId);
            if (streak.isPresent()) {
                return new ResponseEntity<>(streak.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Streak belum ada untuk pengguna ini.", HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Terjadi kesalahan saat mendapatkan streak: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint untuk mendapatkan semua notifikasi pengguna
    // GET /api/users/{userId}/notifications
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

    // Endpoint untuk mendapatkan semua notifikasi pengguna yang belum dibaca
    // GET /api/users/{userId}/notifications/unread
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

    // Endpoint untuk menandai notifikasi sebagai sudah dibaca
    // PUT /api/notifications/{notificationId}/read
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

    // Endpoint untuk mendapatkan semua pengguna (GET /api/users)
    // Endpoint ini sekarang dilindungi oleh JWT
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Endpoint untuk mendapatkan pengguna berdasarkan ID (GET /api/users/{id})
    // Endpoint ini sekarang dilindungi oleh JWT
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        Optional<User> userData = userService.getUserById(id);
        return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoint untuk memperbarui pengguna berdasarkan ID (PUT /api/users/{id})
    // Endpoint ini sekarang dilindungi oleh JWT
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @Valid @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint untuk menghapus pengguna berdasarkan ID (DELETE /api/users/{id})
    // Endpoint ini sekarang dilindungi oleh JWT
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