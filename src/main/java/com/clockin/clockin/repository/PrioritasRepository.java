package com.clockin.clockin.repository;

import com.clockin.clockin.model.Prioritas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrioritasRepository extends JpaRepository<Prioritas, Long> {
}

