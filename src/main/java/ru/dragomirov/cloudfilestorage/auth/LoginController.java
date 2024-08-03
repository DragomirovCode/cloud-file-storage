package ru.dragomirov.cloudfilestorage.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.dragomirov.cloudfilestorage.users.UserService;

@Controller
public class LoginController {
    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String signInPage(){
        return "auth/login";
    }
}
