package ru.dragomirov.cloudfilestorage.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;

    @GetMapping
    public String doGet(@ModelAttribute("user") User user) {
        return "auth/registration";
    }

    @PostMapping
    public String doPost(@ModelAttribute("user") User user) {
        userService.save(user);
        return "redirect:/login";
    }
}
