package com.clockin.clockin.service;

import com.clockin.clockin.model.DataJadwal;

import java.util.List;
import java.util.Optional;

public interface DataJadwalService {
    List<DataJadwal> getAll();
    Optional<DataJadwal> getById(Long id);
    DataJadwal create(DataJadwal jadwal);
    DataJadwal update(Long id, DataJadwal jadwal);
    void delete(Long id);
}
