package com.clockin.clockin.service.impl;

import com.clockin.clockin.model.Task;
import com.clockin.clockin.model.User;
import com.clockin.clockin.dto.TaskDTO;
import com.clockin.clockin.repository.TaskRepository;
import com.clockin.clockin.repository.UserRepository;
import com.clockin.clockin.service.TaskService;
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
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private TaskRepository taskRepository;

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
    private TaskDTO convertToDTO(Task task) {
        if (task == null) {
            return null;
        }
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTanggal(task.getTanggal());
        dto.setJamDeadline(task.getJamDeadline());
        dto.setStatus(task.getStatus());
        return dto;
    }

    // Metode helper untuk mengkonversi DTO ke Entity
    private Task convertToEntity(TaskDTO dto) {
        if (dto == null) {
            return null;
        }
        Task task = new Task();
        task.setId(dto.getId());
        task.setTanggal(dto.getTanggal());
        task.setJamDeadline(dto.getJamDeadline());
        task.setStatus(dto.getStatus());
        return task;
    }

    @Override
    @Transactional
    public TaskDTO createTask(TaskDTO dto) {
        User authenticatedUser = getAuthenticatedUser();
        Task task = convertToEntity(dto);
        task.setUser(authenticatedUser);
        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        User authenticatedUser = getAuthenticatedUser();
        Task task = taskRepository.findByIdAndUser(id, authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan atau Anda tidak memiliki akses."));
        return convertToDTO(task);
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        User authenticatedUser = getAuthenticatedUser();
        List<Task> taskList = taskRepository.findByUser(authenticatedUser);
        return taskList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskDTO updateTask(Long id, TaskDTO dto) {
        User authenticatedUser = getAuthenticatedUser();
        Task existingTask = taskRepository.findByIdAndUser(id, authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Task tidak ditemukan atau Anda tidak memiliki akses."));

        existingTask.setTanggal(dto.getTanggal());
        existingTask.setJamDeadline(dto.getJamDeadline());
        existingTask.setStatus(dto.getStatus());
        
        Task updatedTask = taskRepository.save(existingTask);
        return convertToDTO(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        User authenticatedUser = getAuthenticatedUser();
        if (!taskRepository.existsByIdAndUser(id, authenticatedUser)) {
            throw new RuntimeException("Task tidak ditemukan atau Anda tidak memiliki akses untuk menghapus.");
        }
        taskRepository.deleteById(id);
    }
}
