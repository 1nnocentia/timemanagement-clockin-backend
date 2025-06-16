package com.clockin.clockin.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventDTO {
    private Long id;
    private LocalDate tanggal;
    private LocalTime jamMulai;
    private LocalTime jamAkhir;
}
