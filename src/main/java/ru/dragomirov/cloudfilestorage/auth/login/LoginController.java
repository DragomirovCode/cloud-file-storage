package ru.dragomirov.cloudfilestorage.auth.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.dragomirov.cloudfilestorage.auth.User;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String get(
            @ModelAttribute("user") User user
    ) {
        return "auth/login";
    }
}
