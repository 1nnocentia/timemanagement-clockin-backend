package com.clockin.clockin.service.impl;

import com.clockin.clockin.model.Event;
import com.clockin.clockin.model.User;
import com.clockin.clockin.dto.EventDTO;
import com.clockin.clockin.repository.EventRepository;
import com.clockin.clockin.repository.UserRepository;
import com.clockin.clockin.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String usernameFromPrincipal;

        if (principal instanceof UserDetails) {
            usernameFromPrincipal = ((UserDetails) principal).getUsername();
            logger.info("Authenticated principal is UserDetails. Extracted username: {}", usernameFromPrincipal);
        } else {
            usernameFromPrincipal = principal.toString();
            logger.warn("Authenticated principal is NOT UserDetails (it's {}). Trying toString(): {}", principal.getClass().getName(), usernameFromPrincipal);
        }

        if (usernameFromPrincipal == null || usernameFromPrincipal.isEmpty()) {
            logger.error("Could not extract non-empty username from authenticated principal.");
            throw new RuntimeException("Tidak dapat mengekstrak nama pengguna dari sesi autentikasi.");
        }

        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(usernameFromPrincipal);

        if (userOptional.isEmpty()) {
            logger.error("User with username '{}' (from authenticated session) NOT found in database. This user might be missing or there's a case-sensitivity mismatch.", usernameFromPrincipal);
            throw new RuntimeException("Pengguna terautentikasi tidak ditemukan.");
        }

        return userOptional.get();
    }

    private EventDTO convertToDTO(Event event) {
        if (event == null) {
            return null;
        }
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTanggal(event.getTanggal());
        dto.setJamMulai(event.getJamMulai());
        dto.setJamAkhir(event.getJamAkhir());
        return dto;
    }

    private Event convertToEntity(EventDTO dto) {
        if (dto == null) {
            return null;
        }
        Event event = new Event();
        event.setId(dto.getId());
        event.setTanggal(dto.getTanggal());
        event.setJamMulai(dto.getJamMulai());
        event.setJamAkhir(dto.getJamAkhir());
        return event;
    }

    @Override
    @Transactional
    public EventDTO createEvent(EventDTO dto) {
        User authenticatedUser = getAuthenticatedUser();
        Event event = convertToEntity(dto);
        event.setUser(authenticatedUser);
        Event savedEvent = eventRepository.save(event);
        return convertToDTO(savedEvent);
    }

    @Override
    public EventDTO getEventById(Long id) {
        User authenticatedUser = getAuthenticatedUser();
        Event event = eventRepository.findByIdAndUser(id, authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Event tidak ditemukan atau Anda tidak memiliki akses."));
        return convertToDTO(event);
    }

    @Override
    public List<EventDTO> getAllEvents() {
        User authenticatedUser = getAuthenticatedUser();
        List<Event> eventList = eventRepository.findByUser(authenticatedUser);
        return eventList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventDTO updateEvent(Long id, EventDTO dto) {
        User authenticatedUser = getAuthenticatedUser();
        Event existingEvent = eventRepository.findByIdAndUser(id, authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Event tidak ditemukan atau Anda tidak memiliki akses."));

        existingEvent.setTanggal(dto.getTanggal());
        existingEvent.setJamMulai(dto.getJamMulai());
        existingEvent.setJamAkhir(dto.getJamAkhir());
        
        Event updatedEvent = eventRepository.save(existingEvent);
        return convertToDTO(updatedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        User authenticatedUser = getAuthenticatedUser();
        if (!eventRepository.existsByIdAndUser(id, authenticatedUser)) {
            throw new RuntimeException("Event tidak ditemukan atau Anda tidak memiliki akses untuk menghapus.");
        }
        eventRepository.deleteById(id);
    }
}