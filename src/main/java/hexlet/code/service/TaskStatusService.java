package hexlet.code.service;

import hexlet.code.dto.taskStatuses.TaskStatusCreateDTO;
import hexlet.code.dto.taskStatuses.TaskStatusDTO;
import hexlet.code.dto.taskStatuses.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@AllArgsConstructor
public class TaskStatusService {

    private final TaskStatusRepository tsRepository;
    private final TaskStatusMapper tsMapper;

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
