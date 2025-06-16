package com.clockin.clockin.repository;

import com.clockin.clockin.model.Kategori;
import com.clockin.clockin.model.User;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KategoriRepository extends JpaRepository<Kategori, Long> {
    Optional<Kategori> findByNamaKategoriAndUser(String namaKategori, User user);
    List<Kategori> findByUser(User user);
    Optional<Kategori> findByIdAndUser(Long id, User user);
}

