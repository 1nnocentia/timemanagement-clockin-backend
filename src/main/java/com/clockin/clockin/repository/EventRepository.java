package com.clockin.clockin.repository;

import com.clockin.clockin.model.Event;
import com.clockin.clockin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUser(User user);
    Optional<Event> findByIdAndUser(Long id, User user);
}
