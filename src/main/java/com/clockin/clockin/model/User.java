package com.clockin.clockin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // Entitas JPA
@Table(name = "users") // Nama tabel
@Data // Lombok untuk meng-generate getter, setter, toString, dll
@NoArgsConstructor // Lombok untuk constructor tanpa argumen
@AllArgsConstructor // Lombok untuk constructor dengan semua argumen
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
}
