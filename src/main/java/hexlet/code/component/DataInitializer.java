package hexlet.code.component;

import hexlet.code.configuration.UsersConfig;
import hexlet.code.dto.users.UserCreateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final List<TaskStatus> defaultTaskStatuses;
    private final List<Label> defaultLabels;

    @Autowired
    private UserService userService;

    @Autowired
    private UsersConfig usersConfig;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    public DataInitializer() {
        defaultTaskStatuses = new ArrayList<>();
        defaultTaskStatuses.add(new TaskStatus("Draft", "draft"));
        defaultTaskStatuses.add(new TaskStatus("ToReview", "to_review"));
        defaultTaskStatuses.add(new TaskStatus("ToBeFixed", "to_be_fixed"));
        defaultTaskStatuses.add(new TaskStatus("ToPublish", "to_publish"));
        defaultTaskStatuses.add(new TaskStatus("Published", "published"));

        defaultLabels = new ArrayList<>();
        defaultLabels.add(new Label("feature"));
        defaultLabels.add(new Label("bug"));
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

        // Save default labels
        defaultLabels.stream()
                        .filter(label -> !labelRepository.findByName(label.getName()).isPresent())
                        .map(label -> labelRepository.save(label))
                        .toList();
    }
}
