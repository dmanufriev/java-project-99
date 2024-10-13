package hexlet.code.dto.taskStatuses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
public class TaskStatusDTO {
    private Long id;
    private String name;
    private String slug;
    private String createdAt;
}
