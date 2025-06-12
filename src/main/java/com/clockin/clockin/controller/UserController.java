package com.clockin.clockin.controller;

import com.clockin.clockin.model.User;
import com.clockin.clockin.dto.LoginRequest;
import com.clockin.clockin.dto.LoginResponse;
import com.clockin.clockin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users") // Endpoint untuk pengguna
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint untuk signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
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

    // Endpoint untuk login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        boolean isAuthenticated = userService.authenticateUser(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        if (isAuthenticated) {
            return new ResponseEntity<>(new LoginResponse("Login Berhasil!", true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new LoginResponse("Username/Email atau password salah.", false), HttpStatus.UNAUTHORIZED);
        }
    }

    // Endpoint mengambil semua pengguna
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Endpoint mengambil pengguna berdasarkan ID
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        Optional<User> userData = userService.getUserById(id);
        return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoint memperbarui pengguna
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint menghapus pengguna
    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
