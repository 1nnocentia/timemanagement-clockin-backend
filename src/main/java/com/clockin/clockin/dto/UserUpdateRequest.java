package com.clockin.clockin.dto;


import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;


// Anotasi Lombok @Data untuk menghasilkan getter, setter, toString, equals, dan hashCode
@Data
public class UserUpdateRequest {
    // Nama: Tidak ada @NotBlank, karena opsional untuk update.
    private String nama;

    // Username: Validasi ukuran hanya jika username disediakan
    @Size(min = 3, max = 20, message = "Username harus antara 3 dan 20 karakter")
    private String username;

    // Email: Validasi format email hanya jika email disediakan
    @Email(message = "Format email tidak valid")
    private String email;

    // Password: Validasi kekuatan password hanya jika password disediakan
    @Size(min = 8, message = "Password minimal 8 karakter")
    @Pattern(regexp = ".*[A-Z].*", message = "Password harus mengandung setidaknya satu huruf kapital")
    @Pattern(regexp = ".*[!@#$%^&*()].*", message = "Password harus mengandung setidaknya satu karakter spesial")
    private String password;

    // ID Gambar Profil: Tidak ada validasi khusus, hanya set jika disediakan
    private String profilePictureId;
}