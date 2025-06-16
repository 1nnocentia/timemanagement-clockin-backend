package com.clockin.clockin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "streaks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Streak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading | ugh, i'm lazy T_T
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "current_streak", nullable = false)
    private int currentStreak = 0; 

    @Column(name = "max_streak", nullable = false)
    private int maxStreak = 0;

    @Column(name = "last_interaction_date")
    private LocalDate lastInteractionDate;
}
