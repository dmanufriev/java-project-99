package hexlet.code.app.component;

import hexlet.code.app.configuration.UsersConfig;
import hexlet.code.app.dto.taskStatuses.TaskStatusCreateDTO;
import hexlet.code.app.dto.users.UserCreateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.service.TaskStatusService;
import hexlet.code.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final List<TaskStatusCreateDTO> defaultTaskStatuses;

    @Autowired
    private UserService userService;

    @Autowired
    private UsersConfig usersConfig;

    @Autowired
    private TaskStatusService tsService;

    @Autowired
    public DataInitializer(UserService userService) {
        this.userService = userService;

        defaultTaskStatuses = new ArrayList<>();
        defaultTaskStatuses.add(new TaskStatusCreateDTO("Draft", "draft"));
        defaultTaskStatuses.add(new TaskStatusCreateDTO("ToReview", "to_review"));
        defaultTaskStatuses.add(new TaskStatusCreateDTO("ToBeFixed", "to_be_fixed"));
        defaultTaskStatuses.add(new TaskStatusCreateDTO("ToPublish", "to_publish"));
        defaultTaskStatuses.add(new TaskStatusCreateDTO("Published", "published"));
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

        // Save default task statuses
        defaultTaskStatuses.stream()
                            .map(ts -> tsService.create(ts))
                            .toList();
    }
}
