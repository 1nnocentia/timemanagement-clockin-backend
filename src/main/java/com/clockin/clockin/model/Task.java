package com.clockin.clockin.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_task;

    @Column(nullable = false)
    private LocalDate tanggal;

    @Column(name = "jam_deadline", nullable = false)
    private LocalTime jamDeadline;

    @Column(nullable = false)
    private String status;
}