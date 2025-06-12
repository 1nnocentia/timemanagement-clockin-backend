package com.clockin.clockin.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity // Entitas JPA
@Table(name = "users") // Nama tabel
@Data // Lombok untuk meng-generate getter, setter, toString, dll
@NoArgsConstructor // Lombok untuk constructor tanpa argumen
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Column(name="nama", nullable = false) // Kolom nama, tidak boleh null
    private String nama;

    @NotBlank(message = "Username cannot be blank")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 6 characters long")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[!@#$%^&*()].*", message = "Password must contain at least one special character")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "last_username_change_date")
    private LocalDate lastUsernameChangeDate;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expiry_date")
    private LocalDateTime resetPasswordTokenExpiryDate;

    @Column(name = "profile_picture_id")
    private String profilePictureId;

    public User(String nama, String username, String email, String password) {
        this.nama = nama;
        this.username = username;
        this.email = email;
        this.password = password;
        // Inisialisasi tanggal perubahan sama dengan tanggal hari ini
        this.lastUsernameChangeDate = LocalDate.now();
    }
}
