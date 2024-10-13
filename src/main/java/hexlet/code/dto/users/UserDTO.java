package hexlet.code.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String createdAt;
}
