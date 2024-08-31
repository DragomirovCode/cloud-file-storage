package ru.dragomirov.cloudfilestorage.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController {
    @GetMapping("/error401")
    public String handleError() {
        return "error401";
    }
}

