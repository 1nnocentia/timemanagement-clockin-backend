package com.clockin.clockin.controller;

import com.clockin.clockin.dto.KategoriDTO;
import com.clockin.clockin.service.KategoriService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kategori")
public class KategoriController {

    @Autowired
    private KategoriService kategoriService;

    @PostMapping
    public KategoriDTO create(@RequestBody KategoriDTO dto) {
        return kategoriService.createKategori(dto);
    }

    @GetMapping("/{id}")
    public KategoriDTO getById(@PathVariable Long id) {
        return kategoriService.getKategoriById(id);
    }

    @GetMapping
    public List<KategoriDTO> getAll() {
        return kategoriService.getAllKategori();
    }

    @PutMapping("/{id}")
    public KategoriDTO update(@PathVariable Long id, @RequestBody KategoriDTO dto) {
        return kategoriService.updateKategori(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        kategoriService.deleteKategori(id);
    }
}

