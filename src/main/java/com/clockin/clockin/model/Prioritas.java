package com.clockin.clockin.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prioritas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prioritas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_prioritas;

    @Column(name = "nama_prioritas", nullable = false)
    private String namaPrioritas;  // e.g., Emergency, Urgent, Preventive, Non-Critical

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
