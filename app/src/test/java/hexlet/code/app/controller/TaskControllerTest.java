package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.configuration.UsersConfig;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.service.AuthenticationService;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

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
    private Faker faker;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UsersConfig usersConfig;
    private static String authToken;

    private static final String URL_BASE = "/api/tasks";
    private static final String HEADER_AUTH_NAME = "Authorization";
    private String headerAuthValue;

    private Task taskTest;

//    @BeforeEach
//    public void setUp() throws Exception {
//
//        authToken = authenticationService.getToken(usersConfig.getAdminEmail(), usersConfig.getAdminPassword());
//        headerAuthValue = "Bearer " + authToken;
//
//        taskTest = Instancio.of(Task.class)
//                .ignore(Select.field(Task::getId))
//                .supply(Select.field(Task::getIndex), () -> faker.text().text(5))
//                .supply(Select.field(Task::getName), () -> faker.text().text(5))
//                .supply(Select.field(Task::getDescription), () -> faker.text().text(7))
//                .create();
//    }
//
//    @Test
//    public void testIndex() throws Exception {
//        taskRepository.save(taskTest);
//
//        var result = mockMvc.perform((get(URL_BASE)
//                        .header(HEADER_AUTH_NAME, headerAuthValue)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        var body = result.getResponse().getContentAsString();
//        assertThatJson(body).isArray();
//    }
}
