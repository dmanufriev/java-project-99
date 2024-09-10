package hexlet.code.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.configuration.UsersConfig;
import hexlet.code.app.dto.AuthRequest;
import hexlet.code.app.dto.users.UserCreateDTO;
import hexlet.code.app.model.User;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UsersConfig usersConfig;

    private AuthRequest authRequest;

    @BeforeEach
    public void setUp() {
        authRequest = new AuthRequest();
        authRequest.setUsername(usersConfig.getAdminEmail());
        authRequest.setPassword(usersConfig.getAdminPassword());
    }

    @Test
    public void testCreate() throws Exception {
        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(authRequest));
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }
}
