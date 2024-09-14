package hexlet.code.app.dto.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private Integer index;
    private String title;
    private String content;
    private String status;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    private String createdAt;
}

