package com.clockin.clockin.service;

import com.clockin.clockin.dto.PrioritasDTO;

import java.util.List;

public interface PrioritasService {
    PrioritasDTO createPrioritas(PrioritasDTO dto);
    PrioritasDTO getPrioritasById(Long id);
    List<PrioritasDTO> getAllPrioritas();
    PrioritasDTO updatePrioritas(Long id, PrioritasDTO dto);
    void deletePrioritas(Long id);
}


