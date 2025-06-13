package com.clockin.clockin.service.impl;

import com.clockin.clockin.model.Kategori;
import com.clockin.clockin.repository.KategoriRepository;
import com.clockin.clockin.service.KategoriService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KategoriServiceImpl implements KategoriService {

    private final KategoriRepository repository;

    public KategoriServiceImpl(KategoriRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Kategori> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Kategori> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Kategori create(Kategori kategori) {
        return repository.save(kategori);
    }

    @Override
    public Kategori update(Long id, Kategori updatedKategori) {
        return repository.findById(id).map(k -> {
            k.setNamaKategori(updatedKategori.getNamaKategori());
            return repository.save(k);
        }).orElseThrow(() -> new RuntimeException("Kategori not found"));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
