package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.configuration.UsersConfig;
import hexlet.code.dto.labels.LabelCreateDTO;
import hexlet.code.dto.labels.LabelUpdateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.AuthenticationService;
import hexlet.code.service.TaskService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private Faker faker;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UsersConfig usersConfig;
    private static String authToken;

    private static final String URL_BASE = "/api/labels";
    private static final String HEADER_AUTH_NAME = "Authorization";
    private String headerAuthValue;

    private Label testLabel;
    private LabelCreateDTO testLabelCreateDTO;

    @BeforeEach
    public void setUp() throws Exception {
        authToken = authenticationService.getToken(usersConfig.getAdminEmail(), usersConfig.getAdminPassword());
        headerAuthValue = "Bearer " + authToken;

        labelRepository.deleteAll();

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        testLabelCreateDTO = new LabelCreateDTO();
    }

    @Test
    public void testIndex() throws Exception {

        labelRepository.save(testLabel);

        var result = mockMvc.perform((get(URL_BASE)
                            .header(HEADER_AUTH_NAME, headerAuthValue)))
                            .andExpect(status().isOk())
                            .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        assertThatJson(body.contains(testLabel.getName()));
    }

    @Test
    public void testCreate() throws Exception {
        testLabelCreateDTO.setName(faker.text().text(10));
        var request = post(URL_BASE)
                        .header(HEADER_AUTH_NAME, headerAuthValue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(testLabelCreateDTO));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var label = labelRepository.findByName(testLabelCreateDTO.getName()).get();
        assertThat(label.getName()).isEqualTo(testLabelCreateDTO.getName());
    }

    @Test
    public void testCreateWrongLengthName() throws Exception {
        var wrongLengths = new ArrayList<Integer>(List.of(2, 1001));

        for (var i = 0; i < wrongLengths.size(); i++) {
            testLabelCreateDTO.setName(faker.text().text(wrongLengths.get(i)));
            var request = post(URL_BASE)
                    .header(HEADER_AUTH_NAME, headerAuthValue)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsString(testLabelCreateDTO));
            mockMvc.perform(request)
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void testUpdate() throws Exception {

        labelRepository.save(testLabel);

        var dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("update"));

        var request = put(URL_BASE + "/" + testLabel.getId())
                        .header(HEADER_AUTH_NAME, headerAuthValue)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var labelUpdated = labelRepository.findById(testLabel.getId()).get();
        assertThat(labelUpdated.getName()).isEqualTo(dto.getName().get());
    }

    @Test
    public void testDestroy() throws Exception {
        labelRepository.save(testLabel);
        var request = delete(URL_BASE + "/" + testLabel.getId())
                            .header(HEADER_AUTH_NAME, headerAuthValue);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertThat(labelRepository.existsById(testLabel.getId())).isEqualTo(false);
    }
}
