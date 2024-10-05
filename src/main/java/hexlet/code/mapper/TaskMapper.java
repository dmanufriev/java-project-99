package hexlet.code.mapper;

import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        // Подключение JsonNullableMapper
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status")                  // Call mapSlugsToTaskStatuses
    @Mapping(target = "assignee", source = "assigneeId")                // Call ReferenceMapper
    @Mapping(target = "labels", source = "taskLabelIds")                // Call mapNullableIdsToLabels
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "taskLabelIds", source = "labels")                // Call mapLabelsToIds
    public abstract TaskDTO map(Task model);

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status")                  // Call mapSlugsToTaskStatuses
    @Mapping(target = "assignee", source = "assigneeId")                // Call ReferenceMapper
    @Mapping(target = "labels", source = "taskLabelIds")                // Call mapNullableIdsToLabels
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

    protected TaskStatus mapSlugsToTaskStatuses(String statusSlug) {
        if (statusSlug == null) {
            return null;
        }
        return taskStatusRepository.findBySlug(statusSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found"));
    }

    protected Set<Long> mapLabelsToIds(Set<Label> labels) {
        if (labels == null) {
            return null;
        }
        return labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }

    protected Set<Label> mapNullableIdsToLabels(JsonNullable<Set<Long>> taskLabelIds) {

        if (!taskLabelIds.isPresent() || (null == taskLabelIds.get())) {
            return null;
        }

        var labels = taskLabelIds.get().stream()
                                    .map(id -> labelRepository.findById(id)
                                            .orElseThrow(() -> new ResourceNotFoundException("Label not found")))
                                    .collect(Collectors.toSet());
        return labels;
    }

}
