package hexlet.code.app.dto.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class TaskCreateDTO {
    private Integer index;
    @NotNull
    @Length(min = 1, message = "Length of task name must be greater than 1")
    private String title;
    private String content;
    @NotNull
    private String status;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    private Set<Long> taskLabelIds;

    public void addLabelId(Long newLabelId) {
        if (null == taskLabelIds) {
            taskLabelIds = new HashSet<>();
        }
        taskLabelIds.add(newLabelId);
    }
}
