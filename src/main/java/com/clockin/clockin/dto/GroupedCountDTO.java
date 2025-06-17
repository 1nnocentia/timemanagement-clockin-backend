package com.clockin.clockin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO untuk mengelompokkan jumlah item berdasarkan nama grup
@Data 
@NoArgsConstructor
@AllArgsConstructor
public class GroupedCountDTO {
    private String name;
    private long count;
}
