package hexlet.code.app.service;

import hexlet.code.app.dto.tasks.TaskCreateDTO;
import hexlet.code.app.dto.tasks.TaskDTO;
import hexlet.code.app.dto.tasks.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

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
    private TaskMapper taskMapper;

    public List<TaskDTO> getAll() {
        var tasks = taskRepository.findAll();
        return tasks.stream()
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

        if (taskData.getStatus().isPresent()) {
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

        taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}
