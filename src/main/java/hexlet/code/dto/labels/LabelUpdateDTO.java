package hexlet.code.dto.labels;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class LabelUpdateDTO {
    @NotNull
    @Length(min = 3, max = 1000, message = "Length of label name must be between 3 and 1000")
    private JsonNullable<String> name;
}
