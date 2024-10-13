package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.configuration.UsersConfig;
import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.AuthenticationService;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
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
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ModelGenerator modelGenerator;

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
    private Task testTaskFilter;
    private User testAssignee;
    private TaskStatus testTaskStatus;
    private Label testLabel;

    @BeforeEach
    public void setUp() throws Exception {

        authToken = authenticationService.getToken(usersConfig.getAdminEmail(), usersConfig.getAdminPassword());
        headerAuthValue = "Bearer " + authToken;

        taskRepository.deleteAll();

        testAssignee = Instancio.of(modelGenerator.getUserModel()).create();
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setAssignee(testAssignee);
        testTask.setTaskStatus(testTaskStatus);
        testTask.addLabel(testLabel);
        taskRepository.save(testTask);

        testTaskFilter = Instancio.of(modelGenerator.getTaskModel()).create();
        testTaskFilter.setTaskStatus(Instancio.of(modelGenerator.getTaskStatusModel()).create());
        taskRepository.save(testTaskFilter);
    }

    @Test
    public void testIndex() throws Exception {

        var result = mockMvc.perform((get(URL_BASE)
                            .header(HEADER_AUTH_NAME, headerAuthValue)))
                            .andExpect(status().isOk())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        var tasks = taskRepository.findAll();

        assertThatJson(body).isArray().hasSize(tasks.size());
        for (var task : tasks) {
            var fullDataTask = taskRepository.findByNameWithRelatedEntities(task.getName()).get();
            var taskDTO = taskMapper.map(fullDataTask);
            assertThatJson(body)
                    .isArray()
                    .contains(taskDTO);
        }
    }

    @Test
    public void testIndexWithFilteredAssignee() throws Exception {

        var request = get(URL_BASE + "?assigneeId=" + testAssignee.getId())
                            .header(HEADER_AUTH_NAME, headerAuthValue);
        var result = mockMvc.perform(request)
                            .andExpect(status().isOk())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().hasSize(1);
        assertThatJson(body.contains(testTask.getDescription()));
    }

    @Test
    public void testIndexWithFilteredLabel() throws Exception {

        var request = get(URL_BASE + "?labelId=" + testLabel.getId())
                            .header(HEADER_AUTH_NAME, headerAuthValue);
        var result = mockMvc.perform(request)
                            .andExpect(status().isOk())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().hasSize(1);
        assertThatJson(body.contains(testTask.getDescription()));
    }

    @Test
    public void testIndexWithFilteredStatus() throws Exception {

        var request = get(URL_BASE + "?status=" + testTaskStatus.getSlug())
                            .header(HEADER_AUTH_NAME, headerAuthValue);
        var result = mockMvc.perform(request)
                            .andExpect(status().isOk())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().hasSize(1);
        assertThatJson(body.contains(testTask.getDescription()));
    }

    @Test
    public void testIndexWithFilteredTitle() throws Exception {

        var request = get(URL_BASE + "?titleCont=" + testTask.getName().substring(0, 2))
                            .header(HEADER_AUTH_NAME, headerAuthValue);
        var result = mockMvc.perform(request)
                            .andExpect(status().isOk())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().hasSize(1);
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
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
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
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
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
        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
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
