package com.clockin.clockin.service.impl;

import com.clockin.clockin.dto.PrioritasDTO;
import com.clockin.clockin.model.Prioritas;
import com.clockin.clockin.repository.PrioritasRepository;
import com.clockin.clockin.service.PrioritasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrioritasServiceImpl implements PrioritasService {

    @Autowired
    private PrioritasRepository prioritasRepository;

    private PrioritasDTO mapToDTO(Prioritas entity) {
        PrioritasDTO dto = new PrioritasDTO();
        dto.setId(entity.getId_prioritas());
        dto.setNamaPrioritas(entity.getNamaPrioritas());
        return dto;
    }

    private Prioritas mapToEntity(PrioritasDTO dto) {
        Prioritas entity = new Prioritas();
        entity.setId_prioritas(dto.getId());
        entity.setNamaPrioritas(dto.getNamaPrioritas());
        return entity;
    }

    @Override
    public PrioritasDTO createPrioritas(PrioritasDTO dto) {
        return mapToDTO(prioritasRepository.save(mapToEntity(dto)));
    }

    @Override
    public PrioritasDTO getPrioritasById(Long id) {
        return prioritasRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Prioritas not found"));
    }

    @Override
    public List<PrioritasDTO> getAllPrioritas() {
        return prioritasRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PrioritasDTO updatePrioritas(Long id, PrioritasDTO dto) {
        Prioritas entity = prioritasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prioritas not found"));
        entity.setNamaPrioritas(dto.getNamaPrioritas());
        return mapToDTO(prioritasRepository.save(entity));
    }

    @Override
    public void deletePrioritas(Long id) {
        prioritasRepository.deleteById(id);
    }
}


