package hexlet.code.app.service;

import hexlet.code.app.dto.tasks.TaskCreateDTO;
import hexlet.code.app.dto.tasks.TaskDTO;
import hexlet.code.app.dto.tasks.TaskParamsDTO;
import hexlet.code.app.dto.tasks.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.specification.TaskSpecification;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskSpecification taskSpecification;

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
