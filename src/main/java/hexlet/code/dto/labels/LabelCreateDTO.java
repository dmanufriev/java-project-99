package hexlet.code.dto.labels;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class LabelCreateDTO {
    @NotNull
    @Length(min = 3, max = 1000, message = "Length of label name must be between 3 and 1000")
    private String name;
}
