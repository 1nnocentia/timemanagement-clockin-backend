package com.clockin.clockin.service;

import com.clockin.clockin.dto.DataJadwalDTO;

import java.util.List;

public interface DataJadwalService {
    DataJadwalDTO create(DataJadwalDTO dto);
    DataJadwalDTO getById(Long id);
    List<DataJadwalDTO> getAll();
    DataJadwalDTO update(Long id, DataJadwalDTO dto);
    void delete(Long id);
}
