package com.clockin.clockin.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Notification message
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    // Notification type ("STREAK_MILESTONE", "STREAK_RESET", "REMINDER")
    @Column(name = "type", nullable = false)
    private String type;

    // Notification read status
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false; 

    // Notification creation timestamp
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
