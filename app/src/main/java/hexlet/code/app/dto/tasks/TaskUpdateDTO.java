package hexlet.code.app.dto.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskUpdateDTO {
    private JsonNullable<Integer> index;
    @NotNull
    @Length(min = 1, message = "Length of task name must be greater than 1")
    private JsonNullable<String> title;
    private JsonNullable<String> content;
    @NotNull
    private JsonNullable<String> status;
    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;
}
