package com.clockin.clockin.service;

import com.clockin.clockin.dto.EventDTO;

import java.util.List;

public interface EventService {
    EventDTO createEvent(EventDTO dto);
    EventDTO getEventById(Long id);
    List<EventDTO> getAllEvents();
    EventDTO updateEvent(Long id, EventDTO dto);
    void deleteEvent(Long id);
}
