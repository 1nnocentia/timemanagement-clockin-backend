package com.clockin.clockin.repository;

import com.clockin.clockin.model.Notification;
import com.clockin.clockin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);
}
