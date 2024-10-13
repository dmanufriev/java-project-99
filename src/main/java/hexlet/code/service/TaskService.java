package hexlet.code.service;

import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskParamsDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;

    public List<TaskDTO> getAll(TaskParamsDTO params, int start, int end) {

        int pageSize = end - start;
        if (0 == pageSize) {
            return new ArrayList<>();
        }
        int pageNum = start / pageSize;

        var specification = taskSpecification.build(params);
        var pageTasksDTO = taskRepository.findAll(specification, PageRequest.of(pageNum, pageSize));
        return pageTasksDTO.get()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO create(@Valid TaskCreateDTO taskData) {

        Task task = taskMapper.map(taskData);
        // Set this task to all labels (@ManyToMany link)
        if (null != task.getLabels()) {
            task.getLabels().stream()
                            .map(label -> label.getTasks().add(task))
                            .toList();
        }
        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public TaskDTO findById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return taskMapper.map(task);
    }

    public TaskDTO update(@Valid TaskUpdateDTO taskData, Long id) {

        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Remove this task from all labels (@ManyToMany link)
        if (null != task.getLabels()) {
            task.getLabels().stream()
                    .map(label -> label.getTasks().remove(task))
                    .collect(Collectors.toSet());
        }

        taskMapper.update(taskData, task);

        // Set this task to all labels (@ManyToMany link)
        if (null != task.getLabels()) {
            task.getLabels().stream()
                    .map(label -> label.getTasks().add(task))
                    .collect(Collectors.toSet());
        }

        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
