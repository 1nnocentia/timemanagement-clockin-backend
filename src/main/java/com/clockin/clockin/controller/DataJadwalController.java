package com.clockin.clockin.controller;

import com.clockin.clockin.dto.DataJadwalDTO;
import com.clockin.clockin.service.DataJadwalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data-jadwal")
public class DataJadwalController {

    @Autowired
    private DataJadwalService dataJadwalService;

    @PostMapping
    public DataJadwalDTO create(@RequestBody DataJadwalDTO dto) {
        return dataJadwalService.create(dto);
    }

    @GetMapping("/{id}")
    public DataJadwalDTO getById(@PathVariable Long id) {
        return dataJadwalService.getById(id);
    }

    @GetMapping
    public List<DataJadwalDTO> getAll() {
        return dataJadwalService.getAll();
    }

    @PutMapping("/{id}")
    public DataJadwalDTO update(@PathVariable Long id, @RequestBody DataJadwalDTO dto) {
        return dataJadwalService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        dataJadwalService.delete(id);
    }
}
