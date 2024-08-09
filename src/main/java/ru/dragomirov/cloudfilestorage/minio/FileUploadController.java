package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Controller
public class FileUploadController {
    private final MinioService minioService;

    @Autowired
    public FileUploadController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam(name = "bucketName", defaultValue = "home") String bucketName,
                             @RequestParam(name = "file") MultipartFile file, Model model) {

        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try (InputStream fileStream = file.getInputStream()) {
            minioService.uploadFile(bucketName, uniqueFileName, fileStream);
            model.addAttribute("message", "Файл успешно загружен как " + uniqueFileName);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Ошибка загрузки файла: " + e.getMessage());
        }

        return "redirect:/";
    }

    @GetMapping("/")
    public String getListObjects(@RequestParam(name = "bucketName", defaultValue = "home") String bucketName, Model model) {
        try {
            List<String> objectNames = minioService.listObjects(bucketName).stream()
                    .map(item -> item.objectName())
                    .toList();
            model.addAttribute("objects", objectNames);
            model.addAttribute("bucketName", bucketName);
            return "home";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Ошибка получения списка объектов: " + e.getMessage());
            return "error";
        }
    }

}
