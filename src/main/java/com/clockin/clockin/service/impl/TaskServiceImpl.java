package com.clockin.clockin.service.impl;

import com.clockin.clockin.dto.TaskDTO;
import com.clockin.clockin.model.Task;
import com.clockin.clockin.repository.TaskRepository;
import com.clockin.clockin.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public TaskDTO createTask(TaskDTO dto) {
        Task task = mapToEntity(dto);
        return mapToDTO(taskRepository.save(task));
    }

    @Override
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(task);
    }

    @Override
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO updateTask(Long id, TaskDTO dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTanggal(dto.getTanggal());
        task.setJamDeadline(dto.getJamDeadline());
        task.setStatus(dto.getStatus());

        return mapToDTO(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    private TaskDTO mapToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId_task());
        dto.setTanggal(task.getTanggal());
        dto.setJamDeadline(task.getJamDeadline());
        dto.setStatus(task.getStatus());
        return dto;
    }

    private Task mapToEntity(TaskDTO dto) {
        Task task = new Task();
        task.setId_task(dto.getId());
        task.setTanggal(dto.getTanggal());
        task.setJamDeadline(dto.getJamDeadline());
        task.setStatus(dto.getStatus());
        return task;
    }
}


