package com.clockin.clockin.dto;


import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Data
public class UserUpdateRequest {
    private String nama;

    private String username;

    @Email(message = "Email not valid")
    private String email;

    @Size(min = 8, message = "Password minimal contains 8 characters")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[!@#$%^&*()].*", message = "Password must contain at least one special character")
    private String password;

    // ID Gambar Profil: Tidak ada validasi khusus, hanya set jika disediakan
    private String profilePictureId;
}