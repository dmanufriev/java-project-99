package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.configuration.UsersConfig;
import hexlet.code.app.dto.tasks.TaskCreateDTO;
import hexlet.code.app.dto.tasks.TaskUpdateDTO;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.service.AuthenticationService;
import jakarta.servlet.ServletException;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import java.util.HashSet;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private Faker faker;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UsersConfig usersConfig;
    private static String authToken;

    private static final String URL_BASE = "/api/tasks";
    private static final String HEADER_AUTH_NAME = "Authorization";
    private String headerAuthValue;

    private Task testTask;
    private User testAssignee;
    private TaskStatus testTaskStatus;
    private Label testLabel;

    @BeforeEach
    public void setUp() throws Exception {

        authToken = authenticationService.getToken(usersConfig.getAdminEmail(), usersConfig.getAdminPassword());
        headerAuthValue = "Bearer " + authToken;

        testAssignee = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPasswordDigest), () -> faker.internet().password())
                .create();

        testTaskStatus = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .ignore(Select.field(TaskStatus::getCreatedAt))
                .supply(Select.field(TaskStatus::getName), () -> faker.text().text(5))
                .supply(Select.field(TaskStatus::getSlug), () -> faker.text().text(7))
                .create();

        testLabel = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .ignore(Select.field(Label::getCreatedAt))
                .supply(Select.field(Label::getName), () -> faker.text().text(5))
                .supply(Select.field(Label::getTasks), () -> new HashSet<>())
                .create();

        testTask = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .ignore(Select.field(Task::getTaskStatus))
                .ignore(Select.field(Task::getAssignee))
                .supply(Select.field(Task::getName), () -> faker.text().text(5))
                .supply(Select.field(Task::getDescription), () -> faker.text().text(7))
                .supply(Select.field(Task::getLabels), () -> new HashSet<>())
                .create();

        testTask.setAssignee(testAssignee);
        testTask.setTaskStatus(testTaskStatus);
        testTask.addLabel(testLabel);
        taskRepository.save(testTask);
    }

    @Test
    public void testIndex() throws Exception {

        var result = mockMvc.perform((get(URL_BASE)
                        .header(HEADER_AUTH_NAME, headerAuthValue)))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        assertThatJson(body.contains(testTask.getDescription()));
    }

    @Test
    public void testCreateFullTask() throws Exception {

        TaskCreateDTO taskCreateDTO = new TaskCreateDTO();
        taskCreateDTO.setTitle(faker.text().text(5));
        taskCreateDTO.setContent(faker.text().text(10));
        taskCreateDTO.setStatus(testTaskStatus.getSlug());
        taskCreateDTO.setAssigneeId(testAssignee.getId());
        taskCreateDTO.addLabelId(testLabel.getId());

        var request = post(URL_BASE)
                .header(HEADER_AUTH_NAME, headerAuthValue)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskCreateDTO));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var task = taskRepository.findByNameWithRelatedEntities(taskCreateDTO.getTitle()).get();
        assertThat(task.getAssignee().getId()).isEqualTo(testAssignee.getId());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(testTaskStatus.getSlug());
        assertThat(task.getLabels().size()).isGreaterThan(0);

        // Check many to many link
        var label = labelRepository.findByNameWithRelatedEntities(testLabel.getName()).get();
        var labelTasksCount = label.getTasks().stream()
                                                .filter(t -> t.getId().equals(task.getId()))
                                                .count();
        assertThat(labelTasksCount).isEqualTo(1);
    }

    @Test
    public void testCreateMinTask() throws Exception {

        TaskCreateDTO taskCreateDTO = new TaskCreateDTO();
        taskCreateDTO.setTitle(faker.text().text(5));
        taskCreateDTO.setStatus(testTaskStatus.getSlug());

        var request = post(URL_BASE)
                .header(HEADER_AUTH_NAME, headerAuthValue)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskCreateDTO));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var task = taskRepository.findByNameWithRelatedEntities(taskCreateDTO.getTitle()).get();
        assertThat(task.getAssignee()).isNull();
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(testTaskStatus.getSlug());
        assertThat(task.getLabels()).isEmpty();
    }

    @Test
    public void testCreateWrongName() throws Exception {
        TaskCreateDTO taskCreateDTO = new TaskCreateDTO();
        taskCreateDTO.setTitle("");
        taskCreateDTO.setStatus(testTaskStatus.getSlug());

        var request = post(URL_BASE)
                .header(HEADER_AUTH_NAME, headerAuthValue)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskCreateDTO));
        Throwable thrown = assertThrows(ServletException.class, () -> {
            mockMvc.perform(request);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    public void testCreateWrongStatus() throws Exception {
        TaskCreateDTO taskCreateDTO = new TaskCreateDTO();
        taskCreateDTO.setTitle(faker.text().text(5));
        taskCreateDTO.setStatus(null);

        var request = post(URL_BASE)
                .header(HEADER_AUTH_NAME, headerAuthValue)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskCreateDTO));
        Throwable thrown = assertThrows(ServletException.class, () -> {
            mockMvc.perform(request);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    public void testUpdate() throws Exception {

        var taskUpdateDto = new TaskUpdateDTO();
        taskUpdateDto.setTitle(JsonNullable.of("update"));

        var request = put(URL_BASE + "/" + testTask.getId())
                .header(HEADER_AUTH_NAME, headerAuthValue)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskUpdateDto));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var taskUpdated = taskRepository.findById(testTask.getId()).get();
        assertThat(taskUpdated.getName()).isEqualTo(taskUpdateDto.getTitle().get());
    }

    @Test
    public void testUpdateWrongName() throws Exception {

        var taskUpdateDto = new TaskUpdateDTO();
        taskUpdateDto.setTitle(JsonNullable.of(null));

        var request = put(URL_BASE + "/" + testTask.getId())
                .header(HEADER_AUTH_NAME, headerAuthValue)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskUpdateDto));
        Throwable thrown = assertThrows(ServletException.class, () -> {
            mockMvc.perform(request);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    public void testDestroy() throws Exception {
        var request = delete(URL_BASE + "/" + testTask.getId())
                .header(HEADER_AUTH_NAME, headerAuthValue);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertThat(taskRepository.existsById(testTask.getId())).isEqualTo(false);
    }
}
