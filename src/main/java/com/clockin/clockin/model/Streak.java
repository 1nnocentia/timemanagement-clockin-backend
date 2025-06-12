package com.clockin.clockin.model;

import jakarta.persistence.*; // Import dari jakarta.persistence untuk JPA
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate; // Menggunakan LocalDate untuk menyimpan tanggal saja

// Anotasi @Entity menandakan bahwa kelas ini adalah entitas JPA
@Entity
// Anotasi @Table menentukan nama tabel di database
@Table(name = "streaks")
@Data // Anotasi Lombok untuk menghasilkan getter, setter, toString, equals, dan hashCode
@NoArgsConstructor // Anotasi Lombok untuk menghasilkan konstruktor tanpa argumen
@AllArgsConstructor // Anotasi Lombok untuk menghasilkan konstruktor dengan semua argumen
public class Streak {

    // Anotasi @Id menandakan bahwa properti ini adalah primary key
    @Id
    // Anotasi @GeneratedValue untuk konfigurasi strategi generasi primary key otomatis
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relasi Many-to-One dengan entitas User
    // Setiap Streak terhubung ke satu User
    // @JoinColumn menentukan kolom foreign key di tabel "streaks"
    @ManyToOne(fetch = FetchType.LAZY) // LAZY loading agar User tidak langsung dimuat
    @JoinColumn(name = "user_id", nullable = false) // user_id adalah nama kolom foreign key di tabel streaks
    private User user;

    // Jumlah streak saat ini (hari berturut-turut)
    @Column(name = "current_streak", nullable = false)
    private int currentStreak = 0; // Default 0

    // Jumlah streak paling banyak yang pernah didapatkan
    @Column(name = "max_streak", nullable = false)
    private int maxStreak = 0; // Default 0

    // Tanggal interaksi terakhir pengguna untuk streak ini
    // Menggunakan LocalDate karena kita hanya peduli dengan tanggal (bukan waktu)
    // untuk perhitungan streak harian.
    @Column(name = "last_interaction_date")
    private LocalDate lastInteractionDate;
}
