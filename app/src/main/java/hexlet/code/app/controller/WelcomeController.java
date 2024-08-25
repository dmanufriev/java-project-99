package hexlet.code.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/welcome")
public class WelcomeController {

    @GetMapping(path = "")
    public String welcome() {
        return "Welcome to Spring";
    }
}
