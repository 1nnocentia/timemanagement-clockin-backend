package com.clockin.clockin.service;

import com.clockin.clockin.dto.KategoriDTO;
import java.util.List;

public interface KategoriService {
    KategoriDTO createKategori(KategoriDTO kategoriDTO);
    KategoriDTO getKategoriById(Long id);
    List<KategoriDTO> getAllKategori();
    KategoriDTO updateKategori(Long id, KategoriDTO kategoriDTO);
    void deleteKategori(Long id);
}
