package com.clockin.clockin.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "data_jadwal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataJadwal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_jadwal;

    @Column(name = "judul_jadwal", nullable = false)
    private String judulJadwal;

    @Column(name = "deskripsi_jadwal", columnDefinition = "TEXT")
    private String deskripsiJadwal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_event", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_task", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kategori", nullable = false)
    private Kategori kategori;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prioritas", nullable = false)
    private Prioritas prioritas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
