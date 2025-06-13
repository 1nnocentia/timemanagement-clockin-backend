package com.clockin.clockin.service.impl;

import com.clockin.clockin.model.DataJadwal;
import com.clockin.clockin.repository.DataJadwalRepository;
import com.clockin.clockin.service.DataJadwalService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DataJadwalServiceImpl implements DataJadwalService {

    private final DataJadwalRepository repository;

    public DataJadwalServiceImpl(DataJadwalRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<DataJadwal> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<DataJadwal> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public DataJadwal create(DataJadwal jadwal) {
        return repository.save(jadwal);
    }

    @Override
    public DataJadwal update(Long id, DataJadwal updatedJadwal) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setJudulJadwal(updatedJadwal.getJudulJadwal());
                    existing.setDeskripsiJadwal(updatedJadwal.getDeskripsiJadwal());
                    existing.setEvent(updatedJadwal.getEvent());
                    existing.setTask(updatedJadwal.getTask());
                    existing.setKategori(updatedJadwal.getKategori());
                    existing.setPrioritas(updatedJadwal.getPrioritas());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Jadwal not found"));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

