package com.clockin.clockin.dto;

import lombok.Data;

@Data
public class DataJadwalDTO {
    private Long idJadwal;
    private String judulJadwal;
    private String deskripsiJadwal;
    
    private Long eventId;
    private Long taskId;
    private Long kategoriId;
    private Long prioritasId;
}
