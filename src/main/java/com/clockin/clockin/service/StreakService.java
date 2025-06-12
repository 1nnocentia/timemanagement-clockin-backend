package com.clockin.clockin.service;

import com.clockin.clockin.model.Streak;
import com.clockin.clockin.model.User;
import com.clockin.clockin.repository.StreakRepository;
import com.clockin.clockin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class StreakService {

    @Autowired
    private StreakRepository streakRepository;

    @Autowired
    private UserRepository userRepository; // Untuk cari user

    /**
     * Metode ini mencatat interaksi pengguna dan memperbarui streak.
     * Logika:
     * - Jika interaksi pertama, mulai streak 1.
     * - Jika interaksi dilakukan pada hari yang sama dengan interaksi terakhir, tidak ada perubahan.
     * - Jika interaksi dilakukan pada hari setelah interaksi terakhir (kemarin), streak bertambah 1.
     * - Jika interaksi dilakukan lebih dari sehari setelah interaksi terakhir, streak direset ke 1.
     * - Max streak akan selalu diperbarui jika current streak lebih besar.
     * @param userId ID pengguna yang berinteraksi.
     * @return Objek Streak yang diperbarui.
     */
    @Transactional
    public Streak recordInteraction(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan dengan ID: " + userId));

        // Cari record streak pengguna, jika tidak ada, buat yang baru
        Streak streak = streakRepository.findByUser(user)
                .orElseGet(() -> {
                    Streak newStreak = new Streak();
                    newStreak.setUser(user);
                    return newStreak;
                });

        // Dapatkan tanggal hari ini dalam UTC
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        // Periksa tanggal interaksi terakhir
        if (streak.getLastInteractionDate() == null) {
            // Interaksi pertama kali untuk pengguna ini
            streak.setCurrentStreak(1);
        } else if (streak.getLastInteractionDate().isEqual(today)) {
            // User sudah berinteraksi hari ini, tidak perlu update streak
            System.out.println("Pengguna sudah berinteraksi hari ini. Streak tidak berubah.");
            return streak; // Mengembalikan streak yang sudah ada tanpa perubahan
        } else if (streak.getLastInteractionDate().plusDays(1).isEqual(today)) {
            // Interaksi dilakukan sehari setelah interaksi terakhir (streak berlanjut)
            streak.setCurrentStreak(streak.getCurrentStreak() + 1);
        } else {
            // Interaksi dilakukan lebih dari sehari setelah interaksi terakhir (streak direset)
            System.out.println("Interaksi terputus. Streak direset.");
            streak.setCurrentStreak(1);
        }

        // Perbarui max streak jika current streak lebih besar
        if (streak.getCurrentStreak() > streak.getMaxStreak()) {
            streak.setMaxStreak(streak.getCurrentStreak());
        }

        // Set tanggal interaksi terakhir ke hari ini
        streak.setLastInteractionDate(today);

        // Simpan atau perbarui record streak di database
        return streakRepository.save(streak);
    }

    /**
     * Metode untuk mendapatkan streak pengguna berdasarkan ID pengguna.
     * @param userId ID pengguna.
     * @return Objek Optional<Streak>.
     */
    public Optional<Streak> getStreakByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan dengan ID: " + userId));
        return streakRepository.findByUser(user);
    }
}