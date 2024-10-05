package hexlet.code.app.configuration;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class UsersConfig {

    @Value("${app.users.admin.email}")
    private String adminEmail;
    @Value("${app.users.admin.password}")
    private String adminPassword;
}
