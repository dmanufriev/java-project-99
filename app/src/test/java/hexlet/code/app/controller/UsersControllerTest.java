package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.users.UserCreateDTO;
import hexlet.code.app.dto.users.UserUpdateDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Faker faker;

    private User testUser;
    private UserCreateDTO createUser;
    private ArrayList<String> wrongEmails;
    private ArrayList<String> wrongPasswords;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(User.class)
                            .ignore(Select.field(User::getId))
                            .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                            .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                            .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                            .supply(Select.field(User::getPasswordDigest), () -> faker.internet().password())
                            .create();
        createUser = Instancio.of(UserCreateDTO.class)
                            .supply(Select.field(UserCreateDTO::getFirstName), () -> faker.name().firstName())
                            .supply(Select.field(UserCreateDTO::getLastName), () -> faker.name().lastName())
                            .supply(Select.field(UserCreateDTO::getEmail), () -> faker.internet().emailAddress())
                            .supply(Select.field(UserCreateDTO::getPassword), () -> faker.internet().password())
                            .create();
        wrongEmails = new ArrayList<>(List.of("user@.com", "user.com", "@test.com"));
        wrongEmails.add(null);
        wrongPasswords = new ArrayList<>();
        wrongPasswords.add(null);
        wrongPasswords.add("12");
    }

    @Test
    public void testIndex() throws Exception {
        userRepository.save(testUser);

        var result = mockMvc.perform((get("/api/users")))
                            .andExpect(status().isOk())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        var request = post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(createUser));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(createUser.getEmail()).get();
        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(createUser.getFirstName());
    }

    @Test
    public void testCreateWrongEmail() throws Exception {

        for (var email : wrongEmails) {
            createUser.setEmail(email);
            var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(createUser));
            Throwable thrown = assertThrows(ServletException.class, () -> {
                mockMvc.perform(request);
            });
            assertNotNull(thrown.getMessage());
        }
    }

    @Test
    public void testCreateWrongPassword() throws Exception {
        for (var password : wrongPasswords) {
            createUser.setPassword(password);
            var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(createUser));
            Throwable thrown = assertThrows(ServletException.class, () -> {
                mockMvc.perform(request);
            });
            assertNotNull(thrown.getMessage());
        }
    }

    @Test
    public void testUpdate() throws Exception {
        userRepository.save(testUser);

        var dto = new UserUpdateDTO();
        dto.setFirstName(JsonNullable.of("test name"));

        var request = put("/api/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto));
        mockMvc.perform(request)
                        .andExpect(status().isOk());
        var user = userRepository.findById(testUser.getId()).get();
        assertThat(user.getFirstName()).isEqualTo(dto.getFirstName().get());
    }

    @Test
    public void testUpdateWrongEmail() throws Exception {

        userRepository.save(testUser);
        var dto = new UserUpdateDTO();

        for (var email : wrongEmails) {
            dto.setEmail(JsonNullable.of(email));
            var request = put("/api/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto));
            Throwable thrown = assertThrows(ServletException.class, () -> {
                mockMvc.perform(request);
            });
            assertNotNull(thrown.getMessage());
        }
    }

    @Test
    public void testUpdateWrongPassword() throws Exception {

        userRepository.save(testUser);
        var dto = new UserUpdateDTO();

        for (var password : wrongPasswords) {
            dto.setPassword(JsonNullable.of(password));
            var request = put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));
            Throwable thrown = assertThrows(ServletException.class, () -> {
                mockMvc.perform(request);
            });
            assertNotNull(thrown.getMessage());
        }
    }

    @Test
    public void testShow() throws Exception {
        userRepository.save(testUser);

        var request = get("/api/users/" + testUser.getId());
        var result = mockMvc.perform(request)
                            .andExpect(status().isOk())
                            .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName())
        );
    }

    @Test
    public void testDestroy() throws Exception {
        userRepository.save(testUser);
        var request = delete("/api/users/" + testUser.getId());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertThat(userRepository.existsById(testUser.getId())).isEqualTo(false);
    }


}
