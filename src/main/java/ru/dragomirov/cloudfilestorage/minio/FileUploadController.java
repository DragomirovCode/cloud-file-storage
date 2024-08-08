package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Controller
public class FileUploadController {

    private final MinioService minioService;

    @Autowired
    public FileUploadController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        String bucketName = "asiatrip";

        // Генерируем уникальное имя для файла
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

}
