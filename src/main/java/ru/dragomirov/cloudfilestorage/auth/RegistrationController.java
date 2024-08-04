package ru.dragomirov.cloudfilestorage.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.dragomirov.cloudfilestorage.users.User;
import ru.dragomirov.cloudfilestorage.users.UserService;

@Controller
@RequestMapping("/registration")
public class RegistrationController {
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String doGet(@ModelAttribute("user") User user){
        return "auth/registration";
    }

    @PostMapping
    public String doPost(@ModelAttribute("user") User user) {
        userService.save(user);
        return "redirect:/auth/login";
    }
}
