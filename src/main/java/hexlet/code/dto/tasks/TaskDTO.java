package hexlet.code.dto.tasks;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
public class TaskDTO {
    private Long id;
    private Integer index;
    private String title;
    private String content;
    private String status;
    @JsonProperty("assignee_id")
    private Long assigneeId;
    private Set<Long> taskLabelIds;
    private String createdAt;
}

