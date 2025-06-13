package com.clockin.clockin.controller;

import com.clockin.clockin.model.DataJadwal;
import com.clockin.clockin.service.DataJadwalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jadwal")
@CrossOrigin(origins = "*")
public class DataJadwalController {

    private final DataJadwalService service;

    public DataJadwalController(DataJadwalService service) {
        this.service = service;
    }

    @GetMapping
    public List<DataJadwal> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataJadwal> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DataJadwal> create(@RequestBody DataJadwal jadwal) {
        return ResponseEntity.ok(service.create(jadwal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataJadwal> update(@PathVariable Long id, @RequestBody DataJadwal jadwal) {
        return ResponseEntity.ok(service.update(id, jadwal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}