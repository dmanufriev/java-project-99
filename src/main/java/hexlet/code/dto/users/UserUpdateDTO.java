package hexlet.code.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {
    private JsonNullable<String> firstName;
    private JsonNullable<String> lastName;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email format is not valid")
    private JsonNullable<String> email;
    @NotNull(message = "Password cannot be null")
    @Length(min = 3, message = "Password length must be greater than 3")
    private JsonNullable<String> password;
}
