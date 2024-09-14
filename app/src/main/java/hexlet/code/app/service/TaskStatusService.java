package hexlet.code.app.service;

import hexlet.code.app.dto.taskStatuses.TaskStatusCreateDTO;
import hexlet.code.app.dto.taskStatuses.TaskStatusDTO;
import hexlet.code.app.dto.taskStatuses.TaskStatusUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class TaskStatusService {
    @Autowired
    private TaskStatusRepository tsRepository;

    @Autowired
    private TaskStatusMapper tsMapper;

    public List<TaskStatusDTO> getAll() {
        var taskStatuses = tsRepository.findAll();
        return taskStatuses.stream()
                .map(tsMapper::map)
                .toList();
    }

    public TaskStatusDTO create(@Valid TaskStatusCreateDTO statusData) {
        TaskStatus taskStatus = tsMapper.map(statusData);
        tsRepository.save(taskStatus);
        return tsMapper.map(taskStatus);
    }

    public TaskStatusDTO findById(Long id) {
        var taskStatus = tsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));
        return tsMapper.map(taskStatus);
    }

    public TaskStatusDTO findBySlug(String slug) {
        var taskStatus = tsRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));
        return tsMapper.map(taskStatus);
    }

    public TaskStatusDTO update(@Valid TaskStatusUpdateDTO statusData, Long id) {
        var taskStatus = tsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));
        tsMapper.update(statusData, taskStatus);
        tsRepository.save(taskStatus);
        return tsMapper.map(taskStatus);
    }

    public void delete(Long id) {
        tsRepository.deleteById(id);
    }
}
