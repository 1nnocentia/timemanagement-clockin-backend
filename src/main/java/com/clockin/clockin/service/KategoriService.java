package com.clockin.clockin.service;

import com.clockin.clockin.model.Kategori;

import java.util.List;
import java.util.Optional;

public interface KategoriService {
    List<Kategori> getAll();
    Optional<Kategori> getById(Long id);
    Kategori create(Kategori kategori);
    Kategori update(Long id, Kategori updatedKategori);
    void delete(Long id);
}
