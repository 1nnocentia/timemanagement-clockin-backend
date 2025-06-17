package com.clockin.clockin.service;

import com.clockin.clockin.dto.DataJadwalDTO;
import com.clockin.clockin.dto.GroupedCountDTO;

import java.util.List;

public interface DataJadwalService {
    DataJadwalDTO create(DataJadwalDTO dto);
    DataJadwalDTO getById(Long id);
    List<DataJadwalDTO> getAll();
    DataJadwalDTO update(Long id, DataJadwalDTO dto);
    void delete(Long id);

    // Metode untuk mendapatkan jumlah tugas/event berdasarkan Prioritas
    List<GroupedCountDTO> getCountsByPrioritas();

    // Metode untuk mendapatkan jumlah tugas/event berdasarkan Kategori
    List<GroupedCountDTO> getCountsByKategori();

    // Metode untuk mendapatkan jumlah event per bulan/minggu/tahun
    // periodType: week, month, year
    List<GroupedCountDTO> getEventCountsByPeriod(String periodType);

    // Metode untuk mendapatkan jumlah task per bulan/minggu/tahun
    List<GroupedCountDTO> getTaskCountsByPeriod(String periodType);
}
