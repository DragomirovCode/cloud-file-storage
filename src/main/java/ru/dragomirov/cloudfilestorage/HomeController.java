package ru.dragomirov.cloudfilestorage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String doGet() {
        return "home";
    }
}
