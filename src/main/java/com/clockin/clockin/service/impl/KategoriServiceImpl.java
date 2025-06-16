package com.clockin.clockin.service.impl;

import com.clockin.clockin.dto.KategoriDTO;
import com.clockin.clockin.model.Kategori;
import com.clockin.clockin.repository.KategoriRepository;
import com.clockin.clockin.service.KategoriService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KategoriServiceImpl implements KategoriService {

    @Autowired
    private KategoriRepository kategoriRepository;

    private KategoriDTO mapToDTO(Kategori kategori) {
        KategoriDTO dto = new KategoriDTO();
        dto.setId(kategori.getId_kategori());
        dto.setNamaKategori(kategori.getNamaKategori());
        return dto;
    }

    private Kategori mapToEntity(KategoriDTO dto) {
        Kategori kategori = new Kategori();
        kategori.setId_kategori(dto.getId());
        kategori.setNamaKategori(dto.getNamaKategori());
        return kategori;
    }

    @Override
    public KategoriDTO createKategori(KategoriDTO dto) {
        return mapToDTO(kategoriRepository.save(mapToEntity(dto)));
    }

    @Override
    public KategoriDTO getKategoriById(Long id) {
        return kategoriRepository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Kategori not found"));
    }

    @Override
    public List<KategoriDTO> getAllKategori() {
        return kategoriRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public KategoriDTO updateKategori(Long id, KategoriDTO dto) {
        Kategori kategori = kategoriRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori not found"));
        kategori.setNamaKategori(dto.getNamaKategori());
        return mapToDTO(kategoriRepository.save(kategori));
    }

    @Override
    public void deleteKategori(Long id) {
        kategoriRepository.deleteById(id);
    }
}

