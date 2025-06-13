package com.clockin.clockin.repository;

import com.clockin.clockin.model.DataJadwal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataJadwalRepository extends JpaRepository<DataJadwal, Long> {
}
