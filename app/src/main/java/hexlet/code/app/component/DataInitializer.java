package hexlet.code.app.component;

import hexlet.code.app.configuration.UsersConfig;
import hexlet.code.app.dto.users.UserCreateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final List<TaskStatus> defaultTaskStatuses;

    @Autowired
    private UserService userService;

    @Autowired
    private UsersConfig usersConfig;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    public DataInitializer() {
        defaultTaskStatuses = new ArrayList<>();
        defaultTaskStatuses.add(new TaskStatus("Draft", "draft"));
        defaultTaskStatuses.add(new TaskStatus("ToReview", "to_review"));
        defaultTaskStatuses.add(new TaskStatus("ToBeFixed", "to_be_fixed"));
        defaultTaskStatuses.add(new TaskStatus("ToPublish", "to_publish"));
        defaultTaskStatuses.add(new TaskStatus("Published", "published"));
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
                            .filter(ts -> !taskStatusRepository.findBySlug(ts.getSlug()).isPresent())
                            .map(ts -> taskStatusRepository.save(ts))
                            .toList();
    }
}
