package com.clockin.clockin.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kategori")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kategori {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nama_kategori", nullable = false)
    private String namaKategori;

    @Column(name = "color", nullable = false)
    private String color;  // hex code

    // foreign key to User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
