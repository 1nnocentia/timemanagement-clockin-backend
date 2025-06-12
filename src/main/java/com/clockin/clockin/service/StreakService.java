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

// Anotasi @Service menandakan kelas ini adalah komponen service Spring
@Service
public class StreakService {

    @Autowired
    private StreakRepository streakRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService; // Injeksi MailService

    @Autowired
    private NotificationService notificationService; // Injeksi NotificationService

    // Milestone streak untuk notifikasi
    private static final int[] MILESTONES = {7, 30, 100, 365, 1000};

    /**
     * Metode ini mencatat interaksi pengguna dan memperbarui streak,
     * serta mengirim notifikasi jika diperlukan.
     *
     * @param userId ID pengguna yang berinteraksi.
     * @return Objek Streak yang diperbarui.
     */
    @Transactional // Pastikan operasi ini berjalan dalam satu transaksi database
    public Streak recordInteraction(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan dengan ID: " + userId));

        Streak streak = streakRepository.findByUser(user)
                .orElseGet(() -> {
                    Streak newStreak = new Streak();
                    newStreak.setUser(user);
                    return newStreak;
                });

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        int oldStreak = streak.getCurrentStreak(); // Simpan streak lama untuk perbandingan

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
            // ** Notifikasi Streak Reset **
            String resetMessage = "Streak Anda terputus pada " + oldStreak + " hari. Mari mulai yang baru!";
            notificationService.createNotification(userId, resetMessage, "STREAK_RESET");
            mailService.sendEmail(user.getEmail(), "Streak Terputus di Clockin!", resetMessage);
            streak.setCurrentStreak(1);
        }

        // Perbarui max streak jika current streak lebih besar
        if (streak.getCurrentStreak() > streak.getMaxStreak()) {
            streak.setMaxStreak(streak.getCurrentStreak());
        }

        // Set tanggal interaksi terakhir ke hari ini
        streak.setLastInteractionDate(today);

        // Simpan atau perbarui record streak di database
        Streak updatedStreak = streakRepository.save(streak);

        // ** Notifikasi Milestone Streak **
        // Cek hanya jika streak bertambah
        if (updatedStreak.getCurrentStreak() > oldStreak) {
            for (int milestone : MILESTONES) {
                if (updatedStreak.getCurrentStreak() == milestone) {
                    String milestoneMessage = "Selamat! Anda mencapai streak " + milestone + " hari di Clockin!";
                    notificationService.createNotification(userId, milestoneMessage, "STREAK_MILESTONE");
                    mailService.sendEmail(user.getEmail(), "Selamat atas Streak " + milestone + " Hari Anda!", milestoneMessage);
                    break; // Keluar setelah menemukan milestone yang cocok
                }
            }
        }
        return updatedStreak;
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