package com.clockin.clockin.service.impl;

import com.clockin.clockin.model.*;
import com.clockin.clockin.dto.DataJadwalDTO;
import com.clockin.clockin.dto.EventDTO;
import com.clockin.clockin.dto.GroupedCountDTO;
import com.clockin.clockin.dto.KategoriDTO;
import com.clockin.clockin.dto.PrioritasDTO;
import com.clockin.clockin.dto.TaskDTO;
import com.clockin.clockin.repository.*;
import com.clockin.clockin.service.DataJadwalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter; 
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Comparator;
import java.util.Map;

import java.util.Collections;
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

    // converter
    private EventDTO convertEntityToDto(Event event) {
        if (event == null) return null;
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTanggal(event.getTanggal());
        dto.setJamMulai(event.getJamMulai());
        dto.setJamAkhir(event.getJamAkhir());
        return dto;
    }

    private TaskDTO convertEntityToDto(Task task) {
        if (task == null) return null;
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTanggal(task.getTanggal());
        dto.setJamDeadline(task.getJamDeadline());
        dto.setStatus(task.getStatus());
        return dto;
    }

    private KategoriDTO convertEntityToDto(Kategori kategori) {
        if (kategori == null) return null;
        KategoriDTO dto = new KategoriDTO();
        dto.setId(kategori.getId());
        dto.setNamaKategori(kategori.getNamaKategori());
        dto.setColor(kategori.getColor());
        return dto;
    }
    
    private PrioritasDTO convertEntityToDto(Prioritas prioritas, List<DataJadwal> allUserSchedules) {
        if (prioritas == null) return null;
        
        long totalTasks = allUserSchedules.stream()
            .filter(dj -> dj.getTask() != null && dj.getPrioritas().getId().equals(prioritas.getId()))
            .count();

        long completedTasks = allUserSchedules.stream()
            .filter(dj -> dj.getTask() != null && 
                         "SELESAI".equalsIgnoreCase(dj.getTask().getStatus()) && 
                         dj.getPrioritas().getId().equals(prioritas.getId()))
            .count();

        PrioritasDTO dto = new PrioritasDTO();
        dto.setId(prioritas.getId());
        dto.setNamaPrioritas(prioritas.getNamaPrioritas());
        dto.setColor(prioritas.getColor());
        dto.setTotalTasks((int) totalTasks);
        dto.setCompletedTasks((int) completedTasks);
        
        return dto;
    }

    private DataJadwalDTO convertEntityToDto(DataJadwal dataJadwal, List<DataJadwal> allUserSchedules) {
        if (dataJadwal == null) return null;
        DataJadwalDTO dto = new DataJadwalDTO();
        dto.setIdJadwal(dataJadwal.getId());
        dto.setJudulJadwal(dataJadwal.getJudulJadwal());
        dto.setDeskripsiJadwal(dataJadwal.getDeskripsiJadwal());
        
        dto.setEvent(convertEntityToDto(dataJadwal.getEvent()));
        dto.setTask(convertEntityToDto(dataJadwal.getTask()));
        dto.setKategori(convertEntityToDto(dataJadwal.getKategori()));
        dto.setPrioritas(convertEntityToDto(dataJadwal.getPrioritas(), allUserSchedules));
        
        return dto;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Tidak ada informasi autentikasi yang valid.");
        }
        Object principal = authentication.getPrincipal();
        
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        // Periksa pengguna anonim untuk mencegah error
        if ("anonymousUser".equals(username)) {
            throw new RuntimeException("Akses ditolak untuk pengguna anonim.");
        }

        try {
            Long userId = Long.parseLong(username);
            return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Pengguna terautentikasi dengan ID " + userId + " tidak ditemukan."));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Format ID pengguna tidak valid dalam token autentikasi.", e);
        }
    }

    @Override
    @Transactional
    public DataJadwalDTO create(DataJadwalDTO dto) {
        User user = getAuthenticatedUser();
        
        Event event = (dto.getEventId() != null) ? eventRepository.findById(dto.getEventId()).orElseThrow(() -> new RuntimeException("Event tidak ditemukan")) : null;
        Task task = (dto.getTaskId() != null) ? taskRepository.findById(dto.getTaskId()).orElseThrow(() -> new RuntimeException("Task tidak ditemukan")) : null;
        Kategori kategori = kategoriRepository.findById(dto.getKategoriId()).orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));
        Prioritas prioritas = prioritasRepository.findById(dto.getPrioritasId()).orElseThrow(() -> new RuntimeException("Prioritas tidak ditemukan"));

        DataJadwal dataJadwal = new DataJadwal();
        dataJadwal.setJudulJadwal(dto.getJudulJadwal());
        dataJadwal.setDeskripsiJadwal(dto.getDeskripsiJadwal());
        dataJadwal.setEvent(event);
        dataJadwal.setTask(task);
        dataJadwal.setKategori(kategori);
        dataJadwal.setPrioritas(prioritas);
        dataJadwal.setUser(user);

        DataJadwal savedDataJadwal = dataJadwalRepository.save(dataJadwal);
        
        List<DataJadwal> allSchedules = dataJadwalRepository.findByUserWithAllRelations(user);
        return convertEntityToDto(savedDataJadwal, allSchedules);
    }
    
    @Override
    public DataJadwalDTO getById(Long id) {
        User user = getAuthenticatedUser();
        DataJadwal dataJadwal = dataJadwalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Data Jadwal tidak ditemukan atau bukan milik Anda."));
        List<DataJadwal> allSchedules = dataJadwalRepository.findByUserWithAllRelations(user);
        return convertEntityToDto(dataJadwal, allSchedules);
    }
    
    @Override
    public List<DataJadwalDTO> getAll() {
        User user = getAuthenticatedUser();
        List<DataJadwal> dataJadwalList = dataJadwalRepository.findByUserWithAllRelations(user);
        if (dataJadwalList == null || dataJadwalList.isEmpty()) {
            return Collections.emptyList();
        }
        return dataJadwalList.stream()
                .map(dj -> convertEntityToDto(dj, dataJadwalList))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DataJadwalDTO update(Long id, DataJadwalDTO dto) {
        User user = getAuthenticatedUser();
        DataJadwal existingDataJadwal = dataJadwalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Data Jadwal tidak ditemukan atau bukan milik Anda."));

        if(dto.getJudulJadwal() != null) existingDataJadwal.setJudulJadwal(dto.getJudulJadwal());
        if(dto.getDeskripsiJadwal() != null) existingDataJadwal.setDeskripsiJadwal(dto.getDeskripsiJadwal());
        
        // Logika untuk update relasi jika diperlukan
        if(dto.getKategoriId() != null) {
            Kategori kategori = kategoriRepository.findById(dto.getKategoriId()).orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));
            existingDataJadwal.setKategori(kategori);
        }
        if(dto.getPrioritasId() != null) {
            Prioritas prioritas = prioritasRepository.findById(dto.getPrioritasId()).orElseThrow(() -> new RuntimeException("Prioritas tidak ditemukan"));
            existingDataJadwal.setPrioritas(prioritas);
        }

        DataJadwal updatedDataJadwal = dataJadwalRepository.save(existingDataJadwal);
        List<DataJadwal> allSchedules = dataJadwalRepository.findByUserWithAllRelations(user);
        return convertEntityToDto(updatedDataJadwal, allSchedules);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = getAuthenticatedUser();
        if (!dataJadwalRepository.existsByIdAndUser(id, user)) {
            throw new RuntimeException("Data Jadwal tidak ditemukan atau Anda tidak memiliki akses untuk menghapus.");
        }
        dataJadwalRepository.deleteById(id);
    }
    
    // Metode untuk reports, ditambahkan kembali dengan logika yang benar
    @Override
    public List<GroupedCountDTO> getCountsByPrioritas() {
        User user = getAuthenticatedUser();
        List<Object[]> results = dataJadwalRepository.countByPrioritasNameAndUser(user);
        return results.stream()
            .map(result -> new GroupedCountDTO((String) result[0], (Long) result[1]))
            .collect(Collectors.toList());
    }

    @Override
    public List<GroupedCountDTO> getCountsByKategori() {
        User user = getAuthenticatedUser();
        List<Object[]> results = dataJadwalRepository.countByKategoriNameAndUser(user);
        return results.stream()
            .map(result -> new GroupedCountDTO((String) result[0], (Long) result[1]))
            .collect(Collectors.toList());
    }

    @Override
    public List<GroupedCountDTO> getEventCountsByPeriod(String periodType) {
        User user = getAuthenticatedUser();
        List<DataJadwal> dataJadwalList = dataJadwalRepository.findByUserWithAllRelations(user);

        Map<String, Long> countsMap;

        switch (periodType.toUpperCase()) {
            case "MONTH":
                countsMap = dataJadwalList.stream()
                    .filter(dj -> dj.getEvent() != null && dj.getEvent().getTanggal() != null)
                    .collect(Collectors.groupingBy(
                        dj -> dj.getEvent().getTanggal().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()
                    ));
                break;
            case "WEEK":
                countsMap = dataJadwalList.stream()
                    .filter(dj -> dj.getEvent() != null && dj.getEvent().getTanggal() != null)
                    .collect(Collectors.groupingBy(
                        dj -> {
                            WeekFields weekFields = WeekFields.of(Locale.getDefault());
                            return dj.getEvent().getTanggal().getYear() + "-W" +
                                   String.format("%02d", dj.getEvent().getTanggal().get(weekFields.weekOfWeekBasedYear()));
                        },
                        Collectors.counting()
                    ));
                break;
            case "YEAR":
                countsMap = dataJadwalList.stream()
                    .filter(dj -> dj.getEvent() != null && dj.getEvent().getTanggal() != null)
                    .collect(Collectors.groupingBy(
                        dj -> String.valueOf(dj.getEvent().getTanggal().getYear()),
                        Collectors.counting()
                    ));
                break;
            default:
                throw new IllegalArgumentException("Tipe periode tidak valid: " + periodType);
        }

        return countsMap.entrySet().stream()
            .map(entry -> new GroupedCountDTO(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(GroupedCountDTO::getName)) 
            .collect(Collectors.toList());
    }

    @Override
    public List<GroupedCountDTO> getTaskCountsByPeriod(String periodType) {
        User user = getAuthenticatedUser();
        List<DataJadwal> dataJadwalList = dataJadwalRepository.findByUserWithAllRelations(user); 

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
                            return dj.getTask().getTanggal().getYear() + "-W" +
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

        return countsMap.entrySet().stream()
            .map(entry -> new GroupedCountDTO(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(GroupedCountDTO::getName))
            .collect(Collectors.toList());
    }
}