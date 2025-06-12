package com.clockin.clockin.repository;

import com.clockin.clockin.model.Streak;
import com.clockin.clockin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StreakRepository extends JpaRepository<Streak, Long> {
    Optional<Streak> findByUser(User user);
}
