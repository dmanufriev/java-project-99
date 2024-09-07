package hexlet.code.app.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UserCreateDTO {
    private String firstName;
    private String lastName;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email format is not valid")
    private String email;
    @NotNull(message = "Password cannot be null")
    @Length(min = 3, message = "Password length must be greater than 3")
    private String password;
}
