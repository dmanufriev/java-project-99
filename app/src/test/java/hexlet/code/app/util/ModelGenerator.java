package hexlet.code.app.util;

import hexlet.code.app.dto.users.UserCreateDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Getter
@Component
public class ModelGenerator {
    private Model<User> userModel;
    private Model<UserCreateDTO> userCreateDTOModel;
    private Model<TaskStatus> taskStatusModel;
    private Model<Label> labelModel;
    private Model<Task> taskModel;

    @Autowired
    private Faker faker;

    @PostConstruct
    private void init() {

        userModel = Instancio.of(User.class)
                                .ignore(Select.field(User::getId))
                                .ignore(Select.field(User::getCreatedAt))
                                .ignore(Select.field(User::getUpdatedAt))
                                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                                .supply(Select.field(User::getPasswordDigest), () -> faker.internet().password())
                                .toModel();

        userCreateDTOModel = Instancio.of(UserCreateDTO.class)
                                .supply(Select.field(UserCreateDTO::getFirstName), () -> faker.name().firstName())
                                .supply(Select.field(UserCreateDTO::getLastName), () -> faker.name().lastName())
                                .supply(Select.field(UserCreateDTO::getEmail), () -> faker.internet().emailAddress())
                                .supply(Select.field(UserCreateDTO::getPassword), () -> faker.internet().password())
                                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                                .ignore(Select.field(TaskStatus::getId))
                                .ignore(Select.field(TaskStatus::getCreatedAt))
                                .supply(Select.field(TaskStatus::getName), () -> faker.text().text(5))
                                .supply(Select.field(TaskStatus::getSlug), () -> faker.text().text(7))
                                .toModel();

        labelModel = Instancio.of(Label.class)
                                .ignore(Select.field(Label::getId))
                                .ignore(Select.field(Label::getCreatedAt))
                                .supply(Select.field(Label::getName), () -> faker.text().text(5))
                                .supply(Select.field(Label::getTasks), () -> new HashSet<>())
                                .toModel();

        taskModel = Instancio.of(Task.class)
                                .ignore(Select.field(Task::getId))
                                .ignore(Select.field(Task::getTaskStatus))
                                .ignore(Select.field(Task::getAssignee))
                                .supply(Select.field(Task::getName), () -> faker.text().text(5))
                                .supply(Select.field(Task::getDescription), () -> faker.text().text(7))
                                .supply(Select.field(Task::getLabels), () -> new HashSet<>())
                                .toModel();

    }
}
