package com.clockin.clockin.controller;

import com.clockin.clockin.model.Streak;
import com.clockin.clockin.model.User;
import com.clockin.clockin.dto.ForgotPasswordRequest;
import com.clockin.clockin.dto.LoginRequest;
import com.clockin.clockin.dto.LoginResponse;
import com.clockin.clockin.dto.ResetPasswordRequest;
import com.clockin.clockin.service.StreakService;
import com.clockin.clockin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api") // Endpoint untuk pengguna
public class UserController {

    @Autowired
    private UserService userService;

    // Anotasi @Autowired untuk injeksi dependensi StreakService
    @Autowired
    private StreakService streakService;

    // Endpoint untuk pendaftaran pengguna baru (signup)
    // POST /api/signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            // Perbarui konstruktor user jika Anda menggunakan yang baru
            // User newUser = new User(user.getNama(), user.getUsername(), user.getEmail(), user.getPassword());
            User registeredUser = userService.registerUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Jika username atau email sudah ada
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            // Penanganan error umum
            return new ResponseEntity<>("Terjadi kesalahan saat pendaftaran.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint untuk login pengguna
    // POST /api/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> authenticatedUser = userService.authenticateUser(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        if (authenticatedUser.isPresent()) {
            // Di sini, Anda akan menghasilkan token JWT atau sesi.
            // Untuk demonstrasi, kita hanya memberikan pesan sukses.
            // Contoh JWT: String token = jwtService.generateToken(authenticatedUser.get());
            // String token = "dummy_jwt_token_for_user_" + authenticatedUser.get().getId();
            // Implementasi "remember me" di sisi klien akan menyimpan token ini.
            return new ResponseEntity<>(new LoginResponse("Login Berhasil!", true, "YOUR_GENERATED_JWT_TOKEN", authenticatedUser.get().getId()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new LoginResponse("Username/Email atau password salah.", false, null, null), HttpStatus.UNAUTHORIZED);
        }
    }

    // ** BARU: Endpoint untuk memulai permintaan lupa password **
    // POST /api/forgot-password/request
    @PostMapping("/forgot-password/request")
    public ResponseEntity<String> forgotPasswordRequest(@RequestBody ForgotPasswordRequest request) {
        try {
            // Di sini Anda akan memanggil layanan untuk menghasilkan dan mengirim token.
            // Untuk demonstrasi, kita hanya akan mencetak token ke konsol.
            String token = userService.initiatePasswordReset(request.getEmail());
            // return new ResponseEntity<>("Jika email Anda terdaftar, instruksi reset password telah dikirim.", HttpStatus.OK);
            return new ResponseEntity<>("Token reset password telah dihasilkan untuk email " + request.getEmail() + ": " + token + ". (Cek konsol server untuk token. Dalam produksi, ini akan dikirim via email)", HttpStatus.OK);

        } catch (RuntimeException e) {
            // Untuk keamanan, disarankan untuk tidak memberi tahu apakah email terdaftar atau tidak
            return new ResponseEntity<>("Jika email Anda terdaftar, instruksi reset password telah dikirim. Pesan error: " + e.getMessage(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Terjadi kesalahan saat memproses permintaan reset password.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ** BARU: Endpoint untuk mereset password menggunakan token **
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

    // Endpoint untuk mendapatkan semua pengguna (GET /api/users)
    // Hanya untuk tujuan demonstrasi/admin. Biasanya dilindungi.
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Endpoint untuk mendapatkan pengguna berdasarkan ID (GET /api/users/{id})
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        Optional<User> userData = userService.getUserById(id);
        return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoint untuk memperbarui pengguna berdasarkan ID (PUT /api/users/{id})
    // Perhatikan: endpoint ini sekarang menangani batasan perubahan username dan password hashing.
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Atau status lain yang sesuai
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint untuk menghapus pengguna berdasarkan ID (DELETE /api/users/{id})
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
