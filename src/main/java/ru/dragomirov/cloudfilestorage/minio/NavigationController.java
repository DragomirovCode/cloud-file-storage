package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NavigationController {
    @GetMapping("/back")
    public String get(
            @RequestParam(name = "path", required = false) String path
    ) {
        return "redirect:/?path=" + path;
    }
}
