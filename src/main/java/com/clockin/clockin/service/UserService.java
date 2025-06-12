package com.clockin.clockin.service;

import com.clockin.clockin.model.User;
import com.clockin.clockin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injeksi BCryptPasswordEncoder

    // Menambahkan pengguna baru
    public User registerUser(User user) {
        // Cek apakah username atau email sudah ada
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already exists.");
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already exists.");
        }

        // Hashing password sebelum menyimpan ke database
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Metode untuk proses login
    public boolean authenticateUser(String usernameOrEmail, String password) {
        User user = userRepository.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail);
        }

        // Jika user tidak ditemukan atau password tidak cocok
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return false;
        }
        return true;
    }

    // Metode untuk mendapatkan semua pengguna
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Metode untuk mendapatkan pengguna berdasarkan ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Metode untuk memperbarui pengguna
    public User updateUser(Long id, User userDetails) {
        Optional<User> existingUserOptional = userRepository.findById(id);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            existingUser.setNama(userDetails.getNama());
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setEmail(userDetails.getEmail());
            // Jika password diperbarui, hash password baru
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            return userRepository.save(existingUser);
        }
        return null; // Atau throw exception jika user tidak ditemukan
    }

    // Metode untuk menghapus pengguna
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
