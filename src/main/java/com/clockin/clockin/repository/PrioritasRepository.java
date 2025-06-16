package com.clockin.clockin.repository;

import com.clockin.clockin.model.Kategori;
import com.clockin.clockin.model.Prioritas;
import com.clockin.clockin.model.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrioritasRepository extends JpaRepository<Prioritas, Long> {
    Optional<Kategori> findByNamaPrioritasAndUser(String namaPrioritas, User user);
    List<Kategori> findByUser(User user);
    Optional<Kategori> findByIdAndUser(Long id, User user);
}


