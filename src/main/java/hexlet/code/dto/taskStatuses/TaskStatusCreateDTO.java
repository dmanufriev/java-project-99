package hexlet.code.dto.taskStatuses;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class TaskStatusCreateDTO {

    @NotNull
    @Length(min = 1, message = "Length of task status name must be greater than 1")
    private String name;

    @NotNull
    @Length(min = 1, message = "Length of task status slug must be greater than 1")
    private String slug;

    public TaskStatusCreateDTO() {
    }

    public TaskStatusCreateDTO(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}
