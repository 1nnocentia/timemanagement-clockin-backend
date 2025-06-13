package com.clockin.clockin.controller;

import com.clockin.clockin.model.Prioritas;
import com.clockin.clockin.service.PrioritasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prioritas")
@CrossOrigin(origins = "*")
public class PrioritasController {

    private final PrioritasService service;

    public PrioritasController(PrioritasService service) {
        this.service = service;
    }

    @GetMapping
    public List<Prioritas> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prioritas> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Prioritas> create(@RequestBody Prioritas prioritas) {
        return ResponseEntity.ok(service.create(prioritas));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prioritas> update(@PathVariable Long id, @RequestBody Prioritas prioritas) {
        return ResponseEntity.ok(service.update(id, prioritas));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
