package com.clockin.clockin.service;

import com.clockin.clockin.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    List<Task> getAll();
    Optional<Task> getById(Long id);
    Task create(Task task);
    Task update(Long id, Task updatedTask);
    void delete(Long id);
}
