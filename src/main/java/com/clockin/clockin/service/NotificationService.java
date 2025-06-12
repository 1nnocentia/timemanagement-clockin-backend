package com.clockin.clockin.service;

import com.clockin.clockin.model.Notification;
import com.clockin.clockin.model.User;
import com.clockin.clockin.repository.NotificationRepository;
import com.clockin.clockin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Membuat dan menyimpan notifikasi baru untuk pengguna.
     * @param userId ID pengguna.
     * @param message Pesan notifikasi.
     * @param type Tipe notifikasi (misalnya, "STREAK_MILESTONE", "STREAK_RESET").
     * @return Objek Notifikasi yang dibuat.
     */
    @Transactional
    public Notification createNotification(Long userId, String message, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false); 

        return notificationRepository.save(notification);
    }

    /**
     * Mendapatkan semua notifikasi untuk pengguna tertentu, diurutkan dari yang terbaru.
     * @param userId ID pengguna.
     * @return Daftar Notifikasi.
     */
    public List<Notification> getNotificationsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Mendapatkan semua notifikasi yang belum dibaca untuk pengguna tertentu.
     * @param userId ID pengguna.
     * @return Daftar Notifikasi yang belum dibaca.
     */
    public List<Notification> getUnreadNotificationsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    /**
     * Menandai notifikasi sebagai sudah dibaca.
     * @param notificationId ID notifikasi yang akan ditandai.
     * @return True jika berhasil, false jika tidak ditemukan.
     */
    @Transactional
    public boolean markNotificationAsRead(Long notificationId) {
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);
        if (notificationOptional.isPresent()) {
            Notification notification = notificationOptional.get();
            notification.setRead(true);
            notificationRepository.save(notification);
            return true;
        }
        return false;
    }

    /**
     * Menghapus notifikasi.
     * @param notificationId ID notifikasi yang akan dihapus.
     * @return True jika berhasil, false jika tidak ditemukan.
     */
    @Transactional
    public boolean deleteNotification(Long notificationId) {
        if (notificationRepository.existsById(notificationId)) {
            notificationRepository.deleteById(notificationId);
            return true;
        }
        return false;
    }
}