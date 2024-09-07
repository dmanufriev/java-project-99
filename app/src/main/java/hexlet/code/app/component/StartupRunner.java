package hexlet.code.app.component;

import hexlet.code.app.configuration.UsersConfig;
import hexlet.code.app.dto.users.UserCreateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {

    @Autowired
    private UserService userService;
    @Autowired
    private UsersConfig usersConfig;

    @Autowired
    public StartupRunner(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Save admin as default if not exists at application start
        try {
            userService.findByEmail(usersConfig.getAdminEmail());
        } catch (ResourceNotFoundException e) {
            UserCreateDTO createAdmin = new UserCreateDTO();
            createAdmin.setEmail(usersConfig.getAdminEmail());
            createAdmin.setPassword(usersConfig.getAdminPassword());
            userService.create(createAdmin);
        }
    }
}
