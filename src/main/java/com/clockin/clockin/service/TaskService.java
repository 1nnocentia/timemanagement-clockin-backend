package com.clockin.clockin.service;

import com.clockin.clockin.dto.TaskDTO;

import java.util.List;

public interface TaskService {
    TaskDTO createTask(TaskDTO dto);
    TaskDTO getTaskById(Long id);
    List<TaskDTO> getAllTasks();
    TaskDTO updateTask(Long id, TaskDTO dto);
    void deleteTask(Long id);
}


