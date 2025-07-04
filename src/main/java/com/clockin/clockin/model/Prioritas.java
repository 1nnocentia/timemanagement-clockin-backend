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
    private Long id;

    @Column(name = "nama_prioritas", nullable = false)
    private String namaPrioritas;  // e.g., Emergency, Urgent, Preventive, Non-Critical

    @Column(name = "color", nullable = false)
    private String color;  // hex code

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
