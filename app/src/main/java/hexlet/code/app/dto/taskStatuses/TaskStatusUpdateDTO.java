package hexlet.code.app.dto.taskStatuses;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskStatusUpdateDTO {

    @NotNull
    @Length(min = 1, message = "Length of task status name must be greater than 1")
    private JsonNullable<String> name;

    @NotNull
    @Length(min = 1, message = "Length of task status slug must be greater than 1")
    private JsonNullable<String> slug;
}
