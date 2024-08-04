package ru.dragomirov.cloudfilestorage.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String doGet(@ModelAttribute("user") User user){
        return "auth/login";
    }
}
