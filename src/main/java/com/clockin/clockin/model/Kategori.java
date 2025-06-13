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
    private Long id_kategori;

    @Column(name = "nama_kategori", nullable = false)
    private String namaKategori;
}