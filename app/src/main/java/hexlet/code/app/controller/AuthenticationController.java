package hexlet.code.app.controller;

import hexlet.code.app.dto.AuthRequest;
import hexlet.code.app.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("")
    public String login(@RequestBody AuthRequest authRequest) {
        return authenticationService.getToken(authRequest.getUsername(), authRequest.getPassword());
    }
}
