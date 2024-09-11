package ru.dragomirov.cloudfilestorage.auth.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginErrorController {
    @GetMapping("/error401")
    public String handleError() {
        return "error401";
    }
}

