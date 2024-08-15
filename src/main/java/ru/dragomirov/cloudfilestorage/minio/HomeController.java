package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    private final MinioService minioService;

    @Autowired
    public HomeController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/")
    public String getListObjects(@RequestParam(name = "bucketName", defaultValue = "home") String bucketName, Model model) {
        try {
            List<String> objectNames = minioService.listObjects(bucketName).stream()
                    .map(item -> item.objectName())
                    .toList();
            model.addAttribute("objects", objectNames);
            model.addAttribute("bucketName", bucketName);
            model.addAttribute("newFile", new File());
            return "home";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Ошибка получения списка объектов: " + e.getMessage());
            return "error";
        }
    }
}
