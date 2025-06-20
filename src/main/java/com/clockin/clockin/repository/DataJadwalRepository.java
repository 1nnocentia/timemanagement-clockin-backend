package com.clockin.clockin.repository;

import com.clockin.clockin.model.DataJadwal;
import com.clockin.clockin.model.Prioritas;
import com.clockin.clockin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataJadwalRepository extends JpaRepository<DataJadwal, Long> {
    List<DataJadwal> findByUser(User user);
    Optional<DataJadwal> findByIdAndUser(Long id, User user);
    boolean existsByIdAndUser(Long id, User user);
    List<DataJadwal> findByPrioritasAndUser(Prioritas prioritas, User user);

    // Menghitung jumlah DataJadwal berdasarkan nama Prioritas
    // Return: nama Prioritas, count
    @Query("SELECT p.namaPrioritas, COUNT(dj) FROM DataJadwal dj JOIN dj.prioritas p WHERE dj.user = :user GROUP BY p.namaPrioritas")
    List<Object[]> countByPrioritasNameAndUser(User user);

    // Menghitung jumlah DataJadwal berdasarkan nama Kategori
    // Return: nama Kategori, count
    @Query("SELECT k.namaKategori, COUNT(dj) FROM DataJadwal dj JOIN dj.kategori k WHERE dj.user = :user GROUP BY k.namaKategori")
    List<Object[]> countByKategoriNameAndUser(User user);

    // Mendapatkan semua DataJadwal untuk user tertentu dengan event dan task dimuat
   @Query("SELECT dj FROM DataJadwal dj LEFT JOIN FETCH dj.event LEFT JOIN FETCH dj.task JOIN FETCH dj.kategori JOIN FETCH dj.prioritas WHERE dj.user = :user")
    List<DataJadwal> findByUserWithAllRelations(@Param("user") User user);
}
