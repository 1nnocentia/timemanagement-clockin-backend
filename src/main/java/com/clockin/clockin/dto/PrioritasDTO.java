package com.clockin.clockin.dto;

import lombok.Data;

@Data
public class PrioritasDTO {
    private Long id;
    private String namaPrioritas;
    private int completedTasks;
    private int totalTasks;
    private String color;
}

