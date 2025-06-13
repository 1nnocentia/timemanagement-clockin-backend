package com.clockin.clockin.service.impl;

import com.clockin.clockin.model.Prioritas;
import com.clockin.clockin.repository.PrioritasRepository;
import com.clockin.clockin.service.PrioritasService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrioritasServiceImpl implements PrioritasService {

    private final PrioritasRepository repository;

    public PrioritasServiceImpl(PrioritasRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Prioritas> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Prioritas> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Prioritas create(Prioritas prioritas) {
        return repository.save(prioritas);
    }

    @Override
    public Prioritas update(Long id, Prioritas updatedPrioritas) {
        return repository.findById(id).map(p -> {
            p.setNamaPrioritas(updatedPrioritas.getNamaPrioritas());
            return repository.save(p);
        }).orElseThrow(() -> new RuntimeException("Prioritas not found"));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

