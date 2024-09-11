package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.configuration.UsersConfig;
import hexlet.code.app.dto.taskStatuses.TaskStatusCreateDTO;
import hexlet.code.app.dto.taskStatuses.TaskStatusUpdateDTO;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
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

import java.util.ArrayList;
import java.util.List;

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
public class TaskStatusControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private Faker faker;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UsersConfig usersConfig;
    private static String authToken;

    private TaskStatus taskStatus;
    private List<TaskStatusCreateDTO> wrongTaskStatuses;

    private static final String URL_BASE = "/api/task_statuses";
    private static final String HEADER_AUTH_NAME = "Authorization";
    private String headerAuthValue;

    @BeforeEach
    public void setUp() throws Exception {

        authToken = authenticationService.getToken(usersConfig.getAdminEmail(), usersConfig.getAdminPassword());
        headerAuthValue = "Bearer " + authToken;

        taskStatus = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName), () -> faker.text().text(5))
                .supply(Select.field(TaskStatus::getSlug), () -> faker.text().text(7))
                .create();

        wrongTaskStatuses = new ArrayList<>();
        wrongTaskStatuses.add(new TaskStatusCreateDTO("", "test"));
        wrongTaskStatuses.add(new TaskStatusCreateDTO("Test", ""));
        wrongTaskStatuses.add(new TaskStatusCreateDTO(null, "test"));
        wrongTaskStatuses.add(new TaskStatusCreateDTO("Test", null));
    }

    @Test
    public void testIndex() throws Exception {
        taskStatusRepository.save(taskStatus);

        var result = mockMvc.perform((get(URL_BASE)
                                        .header(HEADER_AUTH_NAME, headerAuthValue)))
                            .andExpect(status().isOk())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        var createTaskStatus = new TaskStatusCreateDTO("Test", "test");
        var request = post(URL_BASE)
                        .header(HEADER_AUTH_NAME, headerAuthValue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(createTaskStatus));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var taskStatus = taskStatusRepository.findBySlug(createTaskStatus.getSlug()).get();
        assertNotNull(taskStatus);
        assertThat(taskStatus.getName()).isEqualTo(createTaskStatus.getName());
    }

    @Test
    public void testCreateWrongTaskStatus() throws Exception {
        for (var dto : wrongTaskStatuses) {
            var request = post(URL_BASE)
                    .header(HEADER_AUTH_NAME, headerAuthValue)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsString(dto));
            Throwable thrown = assertThrows(ServletException.class, () -> {
                mockMvc.perform(request);
            });
            assertNotNull(thrown.getMessage());
        }
    }

    @Test
    public void testUpdate() throws Exception {
        taskStatusRepository.save(taskStatus);

        var dto = new TaskStatusUpdateDTO();
        dto.setName(JsonNullable.of("testName"));

        var request = put(URL_BASE + "/" + taskStatus.getId())
                            .header(HEADER_AUTH_NAME, headerAuthValue)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(dto));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var tsUpdated = taskStatusRepository.findById(taskStatus.getId()).get();
        assertThat(tsUpdated.getName()).isEqualTo(dto.getName().get());
    }

    @Test
    public void testShow() throws Exception {
        taskStatusRepository.save(taskStatus);

        var request = get(URL_BASE + "/" + taskStatus.getId())
                        .header(HEADER_AUTH_NAME, headerAuthValue);
        var result = mockMvc.perform(request)
                            .andExpect(status().isOk())
                            .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(taskStatus.getName()),
                v -> v.node("slug").isEqualTo(taskStatus.getSlug()),
                v -> v.node("createdAt").isEqualTo(taskStatus.getCreatedAt().toString())
        );
    }

    @Test
    public void testDestroy() throws Exception {
        taskStatusRepository.save(taskStatus);
        var request = delete(URL_BASE + "/" + taskStatus.getId())
                        .header(HEADER_AUTH_NAME, headerAuthValue);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertThat(taskStatusRepository.existsById(taskStatus.getId())).isEqualTo(false);
    }

}
