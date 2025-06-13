package com.clockin.clockin.service.impl;

import com.clockin.clockin.model.Task;
import com.clockin.clockin.repository.TaskRepository;
import com.clockin.clockin.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    public TaskServiceImpl(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Task> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Task> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Task create(Task task) {
        return repository.save(task);
    }

    @Override
    public Task update(Long id, Task updatedTask) {
        return repository.findById(id).map(task -> {
            task.setTanggal(updatedTask.getTanggal());
            task.setJamDeadline(updatedTask.getJamDeadline());
            task.setStatus(updatedTask.getStatus());
            return repository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

