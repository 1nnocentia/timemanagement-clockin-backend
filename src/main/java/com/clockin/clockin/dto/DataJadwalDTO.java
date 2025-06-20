package com.clockin.clockin.dto;

import lombok.Data;

@Data
public class DataJadwalDTO {
    private Long eventId;
    private Long taskId;
    private Long kategoriId;
    private Long prioritasId;
    
    private Long idJadwal;
    private String judulJadwal;
    private String deskripsiJadwal;
    
    private EventDTO event;
    private TaskDTO task;
    private KategoriDTO kategori;
    private PrioritasDTO prioritas;
}
