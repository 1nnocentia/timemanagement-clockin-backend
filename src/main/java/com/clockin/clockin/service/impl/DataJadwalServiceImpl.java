package com.clockin.clockin.service.impl;

import com.clockin.clockin.model.*;
import com.clockin.clockin.dto.DataJadwalDTO;
import com.clockin.clockin.repository.*;
import com.clockin.clockin.service.DataJadwalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger; // Import logger
import org.slf4j.LoggerFactory; // Import logger factory

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DataJadwalServiceImpl implements DataJadwalService {

    private static final Logger logger = LoggerFactory.getLogger(DataJadwalServiceImpl.class);

    @Autowired
    private DataJadwalRepository dataJadwalRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private KategoriRepository kategoriRepository;

    @Autowired
    private PrioritasRepository prioritasRepository;

    @Autowired
    private UserRepository userRepository;

    // Metode helper untuk mendapatkan User yang sedang terautentikasi
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

    // Metode helper untuk mengkonversi Entity ke DTO
    private DataJadwalDTO convertToDTO(DataJadwal dataJadwal) {
        if (dataJadwal == null) {
            return null;
        }
        DataJadwalDTO dto = new DataJadwalDTO();
        dto.setIdJadwal(dataJadwal.getId());
        dto.setJudulJadwal(dataJadwal.getJudulJadwal());
        dto.setDeskripsiJadwal(dataJadwal.getDeskripsiJadwal());
        dto.setEventId(dataJadwal.getEvent() != null ? dataJadwal.getEvent().getId() : null);
        dto.setTaskId(dataJadwal.getTask() != null ? dataJadwal.getTask().getId() : null);
        dto.setKategoriId(dataJadwal.getKategori() != null ? dataJadwal.getKategori().getId() : null);
        dto.setPrioritasId(dataJadwal.getPrioritas() != null ? dataJadwal.getPrioritas().getId() : null);
        return dto;
    }

    @Override
    @Transactional
    public DataJadwalDTO create(DataJadwalDTO dto) {
        User authenticatedUser = getAuthenticatedUser();

        // Validasi dan ambil objek Event, Task, Kategori, Prioritas
        Event event = eventRepository.findByIdAndUser(dto.getEventId(), authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Event tidak ditemukan atau bukan milik Anda."));
        Task task = taskRepository.findByIdAndUser(dto.getTaskId(), authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan atau bukan milik Anda."));
        Kategori kategori = kategoriRepository.findByIdAndUser(dto.getKategoriId(), authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan atau bukan milik Anda."));
        Prioritas prioritas = prioritasRepository.findByIdAndUser(dto.getPrioritasId(), authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Prioritas tidak ditemukan atau bukan milik Anda."));

        DataJadwal dataJadwal = new DataJadwal();
        dataJadwal.setJudulJadwal(dto.getJudulJadwal());
        dataJadwal.setDeskripsiJadwal(dto.getDeskripsiJadwal());
        dataJadwal.setEvent(event);
        dataJadwal.setTask(task);
        dataJadwal.setKategori(kategori);
        dataJadwal.setPrioritas(prioritas);
        dataJadwal.setUser(authenticatedUser); // Set user pemilik DataJadwal

        DataJadwal savedDataJadwal = dataJadwalRepository.save(dataJadwal);
        return convertToDTO(savedDataJadwal);
    }

    @Override
    public DataJadwalDTO getById(Long id) {
        User authenticatedUser = getAuthenticatedUser();
        DataJadwal dataJadwal = dataJadwalRepository.findByIdAndUser(id, authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Data Jadwal tidak ditemukan atau Anda tidak memiliki akses."));
        return convertToDTO(dataJadwal);
    }

    @Override
    public List<DataJadwalDTO> getAll() {
        User authenticatedUser = getAuthenticatedUser();
        List<DataJadwal> dataJadwalList = dataJadwalRepository.findByUser(authenticatedUser);
        return dataJadwalList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DataJadwalDTO update(Long id, DataJadwalDTO dto) {
        User authenticatedUser = getAuthenticatedUser();
        DataJadwal existingDataJadwal = dataJadwalRepository.findByIdAndUser(id, authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Data Jadwal tidak ditemukan atau Anda tidak memiliki akses."));

        // Validasi dan ambil objek Event, Task, Kategori, Prioritas jika ID diubah
        if (dto.getEventId() != null && !dto.getEventId().equals(existingDataJadwal.getEvent().getId())) {
            Event event = eventRepository.findByIdAndUser(dto.getEventId(), authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Event tidak ditemukan atau bukan milik Anda."));
            existingDataJadwal.setEvent(event);
        }
        if (dto.getTaskId() != null && !dto.getTaskId().equals(existingDataJadwal.getTask().getId())) {
            Task task = taskRepository.findByIdAndUser(dto.getTaskId(), authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan atau bukan milik Anda."));
            existingDataJadwal.setTask(task);
        }
        if (dto.getKategoriId() != null && !dto.getKategoriId().equals(existingDataJadwal.getKategori().getId())) {
            Kategori kategori = kategoriRepository.findByIdAndUser(dto.getKategoriId(), authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan atau bukan milik Anda."));
            existingDataJadwal.setKategori(kategori);
        }
        if (dto.getPrioritasId() != null && !dto.getPrioritasId().equals(existingDataJadwal.getPrioritas().getId())) {
            Prioritas prioritas = prioritasRepository.findByIdAndUser(dto.getPrioritasId(), authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Prioritas tidak ditemukan atau bukan milik Anda."));
            existingDataJadwal.setPrioritas(prioritas);
        }

        // Perbarui judul dan deskripsi jika disediakan dan tidak kosong
        if (dto.getJudulJadwal() != null && !dto.getJudulJadwal().isEmpty()) {
            existingDataJadwal.setJudulJadwal(dto.getJudulJadwal());
        }
        if (dto.getDeskripsiJadwal() != null && !dto.getDeskripsiJadwal().isEmpty()) {
            existingDataJadwal.setDeskripsiJadwal(dto.getDeskripsiJadwal());
        }
        
        DataJadwal updatedDataJadwal = dataJadwalRepository.save(existingDataJadwal);
        return convertToDTO(updatedDataJadwal);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User authenticatedUser = getAuthenticatedUser();
        if (!dataJadwalRepository.existsByIdAndUser(id, authenticatedUser)) {
            throw new RuntimeException("Data Jadwal tidak ditemukan atau Anda tidak memiliki akses untuk menghapus.");
        }
        dataJadwalRepository.deleteById(id);
    }
}