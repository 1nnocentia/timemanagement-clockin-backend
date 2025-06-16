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
     * create a new notification for a user.
     * @param userId ID
     * @param message
     * @param type notification type (ex, "STREAK_MILESTONE", "STREAK_RESET").
     * @return notification object
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
     * All notifications for a specific user.
     * @param userId ID
     * @return Notification list
     */
    public List<Notification> getNotificationsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * get all unread notifications for a specific user
     * @param userId ID
     * @return unread Notification list
     */
    public List<Notification> getUnreadNotificationsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    /**
     * flagged a notification as read.
     * @param notificationId ID 
     * @return True if success, false jif not found
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
     * delete notification by ID.
     * @param notificationId ID 
     * @return True if success, false jif not found
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