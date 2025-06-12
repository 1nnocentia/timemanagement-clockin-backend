package com.clockin.clockin.service;

import com.clockin.clockin.model.User;
import com.clockin.clockin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injeksi BCryptPasswordEncoder

    // Constraint untuk perubahan username
    private static final int USERNAME_CHANGE_COOLDOWN_DAYS = 30;

    // Masa berlaku token untuk reset password
    private static final long RESET_TOKEN_VALIDITY_MINUTES = 15;

    // Menambahkan pengguna baru (signup)
    @Transactional
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
        user.setLastUsernameChangeDate(LocalDate.now(ZoneOffset.UTC));
        return userRepository.save(user);
    }

    // Metode untuk proses login
    public Optional<User> authenticateUser(String usernameOrEmail, String password) {
        User user = userRepository.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail);
        }

        // Jika user tidak ditemukan atau password tidak cocok
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return Optional.empty();
        }
        return Optional.of(user);
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
    /**
     * Termasuk batasan perubahan username dan hashing password
     * @param id Id pengguna yang akan diperbarui
     * @param userDetails Objek user yang berisi detail baru
     * @return Objek User yang diperbarui
     * @throws RuntimeException kalau user tidak ditemukan atau batasan perubahan username dilanggar
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan dengan ID: " + id));

        // Update Nama
        existingUser.setNama(userDetails.getNama());

        // Update Username
        // Cek apakah username berubah
        if (!existingUser.getUsername().equals(userDetails.getUsername())) {
            // Cek batasan perubahan username
            LocalDate today = LocalDate.now(ZoneOffset.UTC);
            if (existingUser.getLastUsernameChangeDate() != null &&
                existingUser.getLastUsernameChangeDate().plusDays(USERNAME_CHANGE_COOLDOWN_DAYS).isAfter(today)) {
                throw new RuntimeException("Anda hanya dapat mengubah username setiap " + USERNAME_CHANGE_COOLDOWN_DAYS + " hari.");
            }
            // Cek apakah username baru sudah ada
            User userWithNewUsername = userRepository.findByUsername(userDetails.getUsername());
            if (userWithNewUsername != null && !userWithNewUsername.getId().equals(id)) {
                throw new RuntimeException("Username '" + userDetails.getUsername() + "' sudah digunakan oleh pengguna lain.");
            }
            existingUser.setUsername(userDetails.getUsername());
            existingUser.setLastUsernameChangeDate(today); // Perbarui tanggal perubahan terakhir
        }

        // Update Email
        // Cek apakah email berubah
        if (!existingUser.getEmail().equals(userDetails.getEmail())) {
            // Cek apakah email baru sudah ada
            User userWithNewEmail = userRepository.findByEmail(userDetails.getEmail());
            if (userWithNewEmail != null && !userWithNewEmail.getId().equals(id)) {
                throw new RuntimeException("Email '" + userDetails.getEmail() + "' sudah digunakan oleh pengguna lain.");
            }
            existingUser.setEmail(userDetails.getEmail());
        }

        // Update Password (jika disediakan dan tidak kosong)
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    // Metode untuk menghapus pengguna
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Pengguna tidak ditemukan dengan ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Menginisiasi proses lupa password dengan menghasilkan token unik.
     * @param email Email pengguna yang lupa password.
     * @return Token yang dihasilkan.
     * @throws RuntimeException jika email tidak ditemukan.
     */
    @Transactional
    public String initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Pengguna dengan email '" + email + "' tidak ditemukan.");
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(RESET_TOKEN_VALIDITY_MINUTES);

        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiryDate(expiryDate);
        userRepository.save(user);

        // Di sini Anda akan mengirim email ke user dengan token reset password
        // Contoh: sendEmail(user.getEmail(), "Reset Password", "Klik link ini untuk reset password: YOUR_FRONTEND_URL/reset-password?token=" + token);
        System.out.println("Reset token untuk " + email + ": " + token + " (Berlaku hingga: " + expiryDate + ")");
        return token; // Mengembalikan token hanya untuk demonstrasi/debugging. Dalam produksi, tidak dikembalikan ke klien.
    }

    /**
     * Mereset password pengguna menggunakan token yang diberikan.
     * @param token Token reset password.
     * @param newPassword Password baru.
     * @throws RuntimeException jika token tidak valid, kadaluarsa, atau ada masalah lainnya.
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token);

        if (user == null) {
            throw new RuntimeException("Token reset password tidak valid.");
        }

        // Cek apakah token sudah kadaluarsa (dalam UTC)
        if (user.getResetPasswordTokenExpiryDate() == null ||
            user.getResetPasswordTokenExpiryDate().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            // Reset token setelah kadaluarsa atau tidak valid
            user.setResetPasswordToken(null);
            user.setResetPasswordTokenExpiryDate(null);
            userRepository.save(user); // Simpan perubahan untuk membersihkan token yang kadaluarsa
            throw new RuntimeException("Token reset password sudah kadaluarsa atau tidak valid. Silakan minta token baru.");
        }

        // Hashing password baru
        user.setPassword(passwordEncoder.encode(newPassword));
        // Hapus token dan waktu kadaluarsa setelah digunakan
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiryDate(null);
        userRepository.save(user);
    }
}
