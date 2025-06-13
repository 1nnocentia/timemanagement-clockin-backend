package com.clockin.clockin.service.impl;

import com.clockin.clockin.model.Event;
import com.clockin.clockin.repository.EventRepository;
import com.clockin.clockin.service.EventService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository repository;

    public EventServiceImpl(EventRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Event> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Event> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Event create(Event event) {
        return repository.save(event);
    }

    @Override
    public Event update(Long id, Event updatedEvent) {
        return repository.findById(id).map(event -> {
            event.setTanggal(updatedEvent.getTanggal());
            event.setJam_mulai(updatedEvent.getJam_mulai());
            event.setJam_akhir(updatedEvent.getJam_akhir());
            return repository.save(event);
        }).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}


