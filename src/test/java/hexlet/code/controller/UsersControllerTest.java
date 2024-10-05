package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.configuration.UsersConfig;
import hexlet.code.dto.users.UserCreateDTO;
import hexlet.code.dto.users.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
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

import java.util.ArrayList;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private ModelGenerator modelGenerator;

    @Autowired
    private Faker faker;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UsersConfig usersConfig;
    private static String authToken;

    private User testUser;
    private UserCreateDTO createUser;
    private ArrayList<String> wrongEmails;
    private ArrayList<String> wrongPasswords;

    private static final String URL_BASE = "/api/users";
    private static final String HEADER_AUTH_NAME = "Authorization";
    private String headerAuthValue;

    @BeforeEach
    public void setUp() throws Exception {

        authToken = authenticationService.getToken(usersConfig.getAdminEmail(), usersConfig.getAdminPassword());
        headerAuthValue = "Bearer " + authToken;

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        createUser = Instancio.of(modelGenerator.getUserCreateDTOModel()).create();

        wrongEmails = new ArrayList<>(List.of("user@.com", "user.com", "@test.com"));
        wrongEmails.add(null);

        wrongPasswords = new ArrayList<>();
        wrongPasswords.add(null);
        wrongPasswords.add("12");
    }

    @Test
    public void testIndex() throws Exception {
        userRepository.save(testUser);

        var result = mockMvc.perform((get(URL_BASE)
                                        .header(HEADER_AUTH_NAME, headerAuthValue)
                            ))
                            .andExpect(status().isOk())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        var request = post(URL_BASE)
                        .header(HEADER_AUTH_NAME, headerAuthValue)
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
            var request = post(URL_BASE)
                            .header(HEADER_AUTH_NAME, headerAuthValue)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(createUser));
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void testCreateWrongPassword() throws Exception {
        for (var password : wrongPasswords) {
            createUser.setPassword(password);
            var request = post(URL_BASE)
                            .header(HEADER_AUTH_NAME, headerAuthValue)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(createUser));
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void testUpdate() throws Exception {
        userRepository.save(testUser);

        var dto = new UserUpdateDTO();
        dto.setFirstName(JsonNullable.of("test name"));

        var request = put(URL_BASE + "/" + testUser.getId())
                        .header(HEADER_AUTH_NAME, headerAuthValue)
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
            var request = put(URL_BASE + "/" + testUser.getId())
                        .header(HEADER_AUTH_NAME, headerAuthValue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto));
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void testUpdateWrongPassword() throws Exception {

        userRepository.save(testUser);
        var dto = new UserUpdateDTO();

        for (var password : wrongPasswords) {
            dto.setPassword(JsonNullable.of(password));
            var request = put(URL_BASE + "/" + testUser.getId())
                            .header(HEADER_AUTH_NAME, headerAuthValue)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(dto));
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void testShow() throws Exception {
        userRepository.save(testUser);

        var request = get(URL_BASE + "/" + testUser.getId())
                        .header(HEADER_AUTH_NAME, headerAuthValue);
        var result = mockMvc.perform(request)
                            .andExpect(status().isOk())
                            .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("createdAt").isEqualTo(testUser.getCreatedAt().toString())
        );
    }

    @Test
    public void testDestroy() throws Exception {
        userRepository.save(testUser);
        var request = delete(URL_BASE + "/" + testUser.getId())
                        .header(HEADER_AUTH_NAME, headerAuthValue);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertThat(userRepository.existsById(testUser.getId())).isEqualTo(false);
    }


}
