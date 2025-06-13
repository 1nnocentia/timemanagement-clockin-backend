package com.clockin.clockin.controller;

import com.clockin.clockin.model.Kategori;
import com.clockin.clockin.service.KategoriService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kategori")
@CrossOrigin(origins = "*")
public class KategoriController {

    private final KategoriService service;

    public KategoriController(KategoriService service) {
        this.service = service;
    }

    @GetMapping
    public List<Kategori> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kategori> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Kategori> create(@RequestBody Kategori kategori) {
        return ResponseEntity.ok(service.create(kategori));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Kategori> update(@PathVariable Long id, @RequestBody Kategori kategori) {
        return ResponseEntity.ok(service.update(id, kategori));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
