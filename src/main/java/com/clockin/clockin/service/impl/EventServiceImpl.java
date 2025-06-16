package com.clockin.clockin.service.impl;

import com.clockin.clockin.dto.EventDTO;
import com.clockin.clockin.model.Event;
import com.clockin.clockin.repository.EventRepository;
import com.clockin.clockin.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public EventDTO createEvent(EventDTO dto) {
        Event event = mapToEntity(dto);
        return mapToDTO(eventRepository.save(event));
    }

    @Override
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return mapToDTO(event);
    }

    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EventDTO updateEvent(Long id, EventDTO dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setTanggal(dto.getTanggal());
        event.setJam_mulai(dto.getJamMulai());
        event.setJam_akhir(dto.getJamAkhir());

        return mapToDTO(eventRepository.save(event));
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    private EventDTO mapToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTanggal(event.getTanggal());
        dto.setJamMulai(event.getJam_mulai());
        dto.setJamAkhir(event.getJam_akhir());
        return dto;
    }

    private Event mapToEntity(EventDTO dto) {
        Event event = new Event();
        event.setId(dto.getId());
        event.setTanggal(dto.getTanggal());
        event.setJam_mulai(dto.getJamMulai());
        event.setJam_akhir(dto.getJamAkhir());
        return event;
    }
}
