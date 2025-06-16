package com.clockin.clockin.controller;

import com.clockin.clockin.dto.PrioritasDTO;
import com.clockin.clockin.service.PrioritasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prioritas")
public class PrioritasController {

    @Autowired
    private PrioritasService prioritasService;

    @PostMapping
    public PrioritasDTO create(@RequestBody PrioritasDTO dto) {
        return prioritasService.createPrioritas(dto);
    }

    @GetMapping("/{id}")
    public PrioritasDTO getById(@PathVariable Long id) {
        return prioritasService.getPrioritasById(id);
    }

    @GetMapping
    public List<PrioritasDTO> getAll() {
        return prioritasService.getAllPrioritas();
    }

    @PutMapping("/{id}")
    public PrioritasDTO update(@PathVariable Long id, @RequestBody PrioritasDTO dto) {
        return prioritasService.updatePrioritas(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        prioritasService.deletePrioritas(id);
    }
}
