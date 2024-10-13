package hexlet.code.dto.labels;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
public class LabelDTO {
    private Long id;
    private String name;
    private String createdAt;
}
