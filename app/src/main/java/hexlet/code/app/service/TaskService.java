package hexlet.code.app.service;

import hexlet.code.app.dto.tasks.TaskCreateDTO;
import hexlet.code.app.dto.tasks.TaskDTO;
import hexlet.code.app.dto.tasks.TaskParamsDTO;
import hexlet.code.app.dto.tasks.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.User;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        var taskStatus = taskStatusRepository.findBySlug(taskData.getStatus()).get();
        task.setTaskStatus(taskStatus);

        User assignee = null;
        if (null != taskData.getAssigneeId()) {
            assignee = userRepository.findById(taskData.getAssigneeId()).get();
        }
        task.setAssignee(assignee);

        // TODO Нужно перенести бы это в маппер
        if (null != taskData.getTaskLabelIds()) {
            taskData.getTaskLabelIds().stream()
                    .map(labelId -> {
                        Label label = labelRepository.findById(labelId).get();
                        task.getLabels().add(label);
                        var tasks = label.getTasks();
                        tasks.add(task);
                        return label;
                    })
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
        taskMapper.update(taskData, task);

        if ((null != taskData.getStatus()) && taskData.getStatus().isPresent()) {
            var taskStatus = taskStatusRepository.findBySlug(taskData.getStatus().get()).get();
            task.setTaskStatus(taskStatus);
        }

        // TODO Рефакторинг через маппер
        if ((null != taskData.getAssigneeId()) && taskData.getAssigneeId().isPresent()) {
            User assignee = null;
            if (null != taskData.getAssigneeId().get()) {
                assignee = userRepository.findById(taskData.getAssigneeId().get()).get();
            }
            task.setAssignee(assignee);
        }

        if ((null != taskData.getTaskLabelIds()) && taskData.getTaskLabelIds().isPresent()) {

            // Удаляем текущую задачу из всех Labels
            task.getLabels().stream()
                            .map(label -> {
                                label.getTasks().remove(task);
                                labelRepository.save(label);
                                return label;
                            })
                            .toList();

            // Составляем новый список Labels для текущей задачи
            Set<Label> newLabels = new HashSet<>();
            for (Long labelId : taskData.getTaskLabelIds().get()) {
                Label label = labelRepository.findById(labelId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
                newLabels.add(label);
            }
            task.setLabels(newLabels);

            // Добавляем текущую задачу во все Labels
            task.getLabels().stream()
                            .map(label -> {
                                label.getTasks().add(task);
                                labelRepository.save(label);
                                return label;
                            })
                            .toList();
        }

        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
