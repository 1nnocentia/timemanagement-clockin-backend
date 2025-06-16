package com.clockin.clockin.service.impl;

import com.clockin.clockin.dto.DataJadwalDTO;
import com.clockin.clockin.model.*;
import com.clockin.clockin.repository.*;
import com.clockin.clockin.service.DataJadwalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataJadwalServiceImpl implements DataJadwalService {

    @Autowired private DataJadwalRepository dataJadwalRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private KategoriRepository kategoriRepository;
    @Autowired private PrioritasRepository prioritasRepository;

    @Override
    public DataJadwalDTO create(DataJadwalDTO dto) {
        DataJadwal jadwal = mapToEntity(dto);
        return mapToDTO(dataJadwalRepository.save(jadwal));
    }

    @Override
    public DataJadwalDTO getById(Long id) {
        DataJadwal jadwal = dataJadwalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DataJadwal not found"));
        return mapToDTO(jadwal);
}

    @Override
    public List<DataJadwalDTO> getAll() {
        return dataJadwalRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DataJadwalDTO update(Long id, DataJadwalDTO dto) {
        DataJadwal jadwal = dataJadwalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DataJadwal not found"));

        jadwal.setJudulJadwal(dto.getJudulJadwal());
        jadwal.setDeskripsiJadwal(dto.getDeskripsiJadwal());
        jadwal.setEvent(eventRepository.findById(dto.getEventId()).orElseThrow());
        jadwal.setTask(taskRepository.findById(dto.getTaskId()).orElseThrow());
        jadwal.setKategori(kategoriRepository.findById(dto.getKategoriId()).orElseThrow());
        jadwal.setPrioritas(prioritasRepository.findById(dto.getPrioritasId()).orElseThrow());

        return mapToDTO(dataJadwalRepository.save(jadwal));
    }

    @Override
    public void delete(Long id) {
        dataJadwalRepository.deleteById(id);
    }

    private DataJadwal mapToEntity(DataJadwalDTO dto) {
        DataJadwal jadwal = new DataJadwal();
        jadwal.setId_jadwal(dto.getIdJadwal());
        jadwal.setJudulJadwal(dto.getJudulJadwal());
        jadwal.setDeskripsiJadwal(dto.getDeskripsiJadwal());
        jadwal.setEvent(eventRepository.findById(dto.getEventId()).orElseThrow());
        jadwal.setTask(taskRepository.findById(dto.getTaskId()).orElseThrow());
        jadwal.setKategori(kategoriRepository.findById(dto.getKategoriId()).orElseThrow());
        jadwal.setPrioritas(prioritasRepository.findById(dto.getPrioritasId()).orElseThrow());
        return jadwal;
    }

    private DataJadwalDTO mapToDTO(DataJadwal jadwal) {
        DataJadwalDTO dto = new DataJadwalDTO();
        dto.setIdJadwal(jadwal.getId_jadwal());
        dto.setJudulJadwal(jadwal.getJudulJadwal());
        dto.setDeskripsiJadwal(jadwal.getDeskripsiJadwal());
        dto.setEventId(jadwal.getEvent().getId_event());
        dto.setTaskId(jadwal.getTask().getId_task());
        dto.setKategoriId(jadwal.getKategori().getId_kategori());
        dto.setPrioritasId(jadwal.getPrioritas().getId_prioritas());
        return dto;
    }
}
