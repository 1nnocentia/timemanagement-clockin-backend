package com.clockin.clockin.service.impl;

import com.clockin.clockin.model.*;
import com.clockin.clockin.dto.DataJadwalDTO;
import com.clockin.clockin.dto.GroupedCountDTO;
import com.clockin.clockin.repository.*;
import com.clockin.clockin.service.DataJadwalService;
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
import java.time.format.DateTimeFormatter; 
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Comparator;
import java.util.Map;

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

        Event event = null;
        Task task = null;

        if (dto.getEventId() != null) {
            event = eventRepository.findByIdAndUser(dto.getEventId(), authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Error: Event dengan ID " + dto.getEventId() + " tidak ditemukan."));
        }
        if (dto.getTaskId() != null) {
            task = taskRepository.findByIdAndUser(dto.getTaskId(), authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Error: Task dengan ID " + dto.getTaskId() + " tidak ditemukan."));
        }

        // Event event = eventRepository.findByIdAndUser(dto.getEventId(), authenticatedUser)
        //         .orElseThrow(() -> new RuntimeException("Event tidak ditemukan atau bukan milik Anda."));
        // Task task = taskRepository.findByIdAndUser(dto.getTaskId(), authenticatedUser)
        //         .orElseThrow(() -> new RuntimeException("Task tidak ditemukan atau bukan milik Anda."));
        Kategori kategori = kategoriRepository.findByIdAndUser(dto.getKategoriId(), authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Error: Kategori dengan ID " + dto.getKategoriId() + " tidak ditemukan."));
        Prioritas prioritas = prioritasRepository.findByIdAndUser(dto.getPrioritasId(), authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Error: Prioritas dengan ID " + dto.getPrioritasId() + " tidak ditemukan."));

        DataJadwal dataJadwal = new DataJadwal();
        dataJadwal.setJudulJadwal(dto.getJudulJadwal());
        dataJadwal.setDeskripsiJadwal(dto.getDeskripsiJadwal());
        dataJadwal.setEvent(event);
        dataJadwal.setTask(task);
        dataJadwal.setKategori(kategori);
        dataJadwal.setPrioritas(prioritas);
        dataJadwal.setUser(authenticatedUser);

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

        // if (dto.getEventId() != null && !dto.getEventId().equals(existingDataJadwal.getEvent().getId())) {
        //     Event event = eventRepository.findByIdAndUser(dto.getEventId(), authenticatedUser)
        //         .orElseThrow(() -> new RuntimeException("Event tidak ditemukan atau bukan milik Anda."));
        //     existingDataJadwal.setEvent(event);
        // }
        // if (dto.getTaskId() != null && !dto.getTaskId().equals(existingDataJadwal.getTask().getId())) {
        //     Task task = taskRepository.findByIdAndUser(dto.getTaskId(), authenticatedUser)
        //         .orElseThrow(() -> new RuntimeException("Task tidak ditemukan atau bukan milik Anda."));
        //     existingDataJadwal.setTask(task);
        // }
        if (dto.getEventId() != null) {
             Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new RuntimeException("Event tidak ditemukan."));
            existingDataJadwal.setEvent(event);
            existingDataJadwal.setTask(null);
        } else if (dto.getTaskId() != null) {
            Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan."));
            existingDataJadwal.setTask(task);
            existingDataJadwal.setEvent(null);
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

    @Override
    public List<GroupedCountDTO> getCountsByPrioritas() {
        User authenticatedUser = getAuthenticatedUser();
        // query untuk menghitung jumlah DataJadwal berdasarkan Prioritas
        List<Object[]> results = dataJadwalRepository.countByPrioritasNameAndUser(authenticatedUser);
        
        return results.stream()
            .map(result -> new GroupedCountDTO((String) result[0], (Long) result[1]))
            .collect(Collectors.toList());
    }

    @Override
    public List<GroupedCountDTO> getCountsByKategori() {
        User authenticatedUser = getAuthenticatedUser();
        // query untuk menghitung jumlah DataJadwal berdasarkan Kategori
        List<Object[]> results = dataJadwalRepository.countByKategoriNameAndUser(authenticatedUser);

        return results.stream()
            .map(result -> new GroupedCountDTO((String) result[0], (Long) result[1]))
            .collect(Collectors.toList());
    }

    @Override
    public List<GroupedCountDTO> getEventCountsByPeriod(String periodType) {
        User authenticatedUser = getAuthenticatedUser();
        List<DataJadwal> dataJadwalList = dataJadwalRepository.findByUserWithEventAndTask(authenticatedUser);

        // agregasi berdasarkan periodType
        Map<String, Long> countsMap;

        switch (periodType.toUpperCase()) {
            case "MONTH":
                countsMap = dataJadwalList.stream()
                    .filter(dj -> dj.getEvent() != null && dj.getEvent().getTanggal() != null)
                    .collect(Collectors.groupingBy(
                        dj -> dj.getEvent().getTanggal().format(DateTimeFormatter.ofPattern("yyyy-MM")), // Format YYYY-MM
                        Collectors.counting()
                    ));
                break;
            case "WEEK":
                countsMap = dataJadwalList.stream()
                    .filter(dj -> dj.getEvent() != null && dj.getEvent().getTanggal() != null)
                    .collect(Collectors.groupingBy(
                        dj -> {
                            // Format YYYY-WW (tahun-minggu)
                            WeekFields weekFields = WeekFields.of(Locale.getDefault());
                            return dj.getEvent().getTanggal().getYear() + "-" +
                                   String.format("%02d", dj.getEvent().getTanggal().get(weekFields.weekOfWeekBasedYear()));
                        },
                        Collectors.counting()
                    ));
                break;
            case "YEAR":
                countsMap = dataJadwalList.stream()
                    .filter(dj -> dj.getEvent() != null && dj.getEvent().getTanggal() != null)
                    .collect(Collectors.groupingBy(
                        dj -> String.valueOf(dj.getEvent().getTanggal().getYear()), // Format YYYY
                        Collectors.counting()
                    ));
                break;
            default:
                throw new IllegalArgumentException("Tipe periode tidak valid: " + periodType);
        }

        // Konversi Map ke List<GroupedCountDTO> dan urutkan
        return countsMap.entrySet().stream()
            .map(entry -> new GroupedCountDTO(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(GroupedCountDTO::getName)) 
            .collect(Collectors.toList());
    }

    @Override
    public List<GroupedCountDTO> getTaskCountsByPeriod(String periodType) {
        User authenticatedUser = getAuthenticatedUser();
        List<DataJadwal> dataJadwalList = dataJadwalRepository.findByUserWithEventAndTask(authenticatedUser); 

        Map<String, Long> countsMap;

        switch (periodType.toUpperCase()) {
            case "MONTH":
                countsMap = dataJadwalList.stream()
                    .filter(dj -> dj.getTask() != null && dj.getTask().getTanggal() != null)
                    .collect(Collectors.groupingBy(
                        dj -> dj.getTask().getTanggal().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()
                    ));
                break;
            case "WEEK":
                countsMap = dataJadwalList.stream()
                    .filter(dj -> dj.getTask() != null && dj.getTask().getTanggal() != null)
                    .collect(Collectors.groupingBy(
                        dj -> {
                            WeekFields weekFields = WeekFields.of(Locale.getDefault());
                            return dj.getTask().getTanggal().getYear() + "-" +
                                   String.format("%02d", dj.getTask().getTanggal().get(weekFields.weekOfWeekBasedYear()));
                        },
                        Collectors.counting()
                    ));
                break;
            case "YEAR":
                countsMap = dataJadwalList.stream()
                    .filter(dj -> dj.getTask() != null && dj.getTask().getTanggal() != null)
                    .collect(Collectors.groupingBy(
                        dj -> String.valueOf(dj.getTask().getTanggal().getYear()),
                        Collectors.counting()
                    ));
                break;
            default:
                throw new IllegalArgumentException("Tipe periode tidak valid: " + periodType);
        }

        // map ke list GroupedCountDTO dan urutkan
        return countsMap.entrySet().stream()
            .map(entry -> new GroupedCountDTO(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(GroupedCountDTO::getName))
            .collect(Collectors.toList());
    }
}