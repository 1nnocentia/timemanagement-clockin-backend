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
    Optional<Prioritas> findByNamaPrioritasAndUser(String namaPrioritas, User user);
    List<Prioritas> findByUser(User user);
    Optional<Prioritas> findByIdAndUser(Long id, User user);

    boolean existsByIdAndUser(Long id, User user);
}
