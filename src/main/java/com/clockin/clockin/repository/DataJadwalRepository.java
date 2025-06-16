package com.clockin.clockin.repository;

import com.clockin.clockin.model.DataJadwal;
import com.clockin.clockin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataJadwalRepository extends JpaRepository<DataJadwal, Long> {
    List<DataJadwal> findByUser(User user);
    Optional<DataJadwal> findByIdAndUser(Long id, User user);
    boolean existsByIdAndUser(Long id, User user);
}
