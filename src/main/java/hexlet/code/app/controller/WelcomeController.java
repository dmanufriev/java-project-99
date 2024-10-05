package hexlet.code.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import io.sentry.Sentry;

@RestController
@RequestMapping("/welcome")
public class WelcomeController {

    @GetMapping(path = "")
    public String welcome() {

        try {
            throw new Exception("This is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }

        return "Welcome to Spring";
    }
}
