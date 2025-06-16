package com.clockin.clockin.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TaskDTO {
    private Long id;
    private LocalDate tanggal;
    private LocalTime jamDeadline;
    private String status;
}

