package ru.dragomirov.cloudfilestorage.auth.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import ru.dragomirov.cloudfilestorage.auth.User;
import ru.dragomirov.cloudfilestorage.auth.UserDto;
import ru.dragomirov.cloudfilestorage.auth.UserMapper;
import ru.dragomirov.cloudfilestorage.auth.UserService;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public String get(
            @ModelAttribute("user") User user
    ) {
        return "auth/registration";
    }

    @PostMapping
    public String post(
            @Valid @ModelAttribute("user") UserDto userDto,
                         BindingResult bindingResult
            ) {
        if (bindingResult.hasErrors()) {
            return "auth/registration";
        }

        User user = userMapper.toEntity(userDto);
        userService.save(user);
        return "redirect:/login";
    }
}
