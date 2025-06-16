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
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private NotificationService notificationService;

    // milestone streaks
    private static final int[] MILESTONES = {7, 30, 100, 365, 1000};

    /**
     * record interaction and update streak
     * send notifications for milestones and resets
     *
     * @param userId ID 
     * @return streak obhect after update
     */
    @Transactional 
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
        int oldStreak = streak.getCurrentStreak();

        if (streak.getLastInteractionDate() == null) {
            // first interaction in the day (streak +1)
            streak.setCurrentStreak(1);
        } else if (streak.getLastInteractionDate().isEqual(today)) {
            // user has already interacted today (streak unchanged)
            System.out.println("User already interacted today. Streak unchanged.");
            return streak;
        } else if (streak.getLastInteractionDate().plusDays(1).isEqual(today)) {
            // Interaction done the next day (streak +1)
            streak.setCurrentStreak(streak.getCurrentStreak() + 1);
        } else {
            // Skipped intaction (reset streak)
            System.out.println("Streak reset due to skipped interaction.");
            String resetMessage = "Your streak was reset from " + oldStreak + " days. Let's create a new one!";
            notificationService.createNotification(userId, resetMessage, "STREAK_RESET");
            mailService.sendEmail(user.getEmail(), "Your streak being reset in Clockin :'(!", resetMessage);
            streak.setCurrentStreak(1);
        }

        // max streak update if max streak is less than current streak
        if (streak.getCurrentStreak() > streak.getMaxStreak()) {
            streak.setMaxStreak(streak.getCurrentStreak());
        }

        // set last interaction date to today
        streak.setLastInteractionDate(today);

        // save the updated streak
        Streak updatedStreak = streakRepository.save(streak);

        // check for milestones
        if (updatedStreak.getCurrentStreak() > oldStreak) {
            for (int milestone : MILESTONES) {
                if (updatedStreak.getCurrentStreak() == milestone) {
                    String milestoneMessage = "CONGRATULATION! YOU'RE ACHIEVING " + milestone + " DAYS STREAK IN Clockin!";
                    notificationService.createNotification(userId, milestoneMessage, "STREAK_MILESTONE");
                    mailService.sendEmail(user.getEmail(), "CONGRATULATION FOR YOUR  " + milestone + " DAYS STREAK!", milestoneMessage);
                    break;
                }
            }
        }
        return updatedStreak;
    }

    /**
     * streaks by user ID.
     * @param userId ID 
     * @return Optional<Streak>
     */
    public Optional<Streak> getStreakByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan dengan ID: " + userId));
        return streakRepository.findByUser(user);
    }
}