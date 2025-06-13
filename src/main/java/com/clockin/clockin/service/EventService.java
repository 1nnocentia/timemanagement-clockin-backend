package com.clockin.clockin.service;

import com.clockin.clockin.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<Event> getAll();
    Optional<Event> getById(Long id);
    Event create(Event event);
    Event update(Long id, Event updatedEvent);
    void delete(Long id);
}