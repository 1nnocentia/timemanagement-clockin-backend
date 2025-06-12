package com.clockin.clockin.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name="nama", nullable = false) // Kolom nama, tidak boleh null
    private String nama;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "last_username_change_date")
    private LocalDate lastUsernameChangeDate;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expiry_date")
    private LocalDateTime resetPasswordTokenExpiryDate;

    public User(String nama, String username, String email, String password) {
        this.nama = nama;
        this.username = username;
        this.email = email;
        this.password = password;
        // Inisialisasi tanggal perubahan sama dengan tanggal hari ini
        this.lastUsernameChangeDate = LocalDate.now();
    }
}
