package com.clockin.clockin.service;

import com.clockin.clockin.model.Prioritas;

import java.util.List;
import java.util.Optional;

public interface PrioritasService {
    List<Prioritas> getAll();
    Optional<Prioritas> getById(Long id);
    Prioritas create(Prioritas prioritas);
    Prioritas update(Long id, Prioritas updatedPrioritas);
    void delete(Long id);
}

