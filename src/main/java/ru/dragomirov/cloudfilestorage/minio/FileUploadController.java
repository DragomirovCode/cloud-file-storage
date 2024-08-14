package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Controller
public class FileUploadController {
    private final MinioService minioService;

    @Autowired
    public FileUploadController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/upload")
    public String uploadFiles(@RequestParam(name = "bucketName", defaultValue = "home") String bucketName,
                              @RequestParam(name = "files") MultipartFile[] files, Model model) {

        for (MultipartFile file : files) {
            try {
                InputStream fileStream = file.getInputStream();

                String objectName = file.getOriginalFilename();
                minioService.uploadFile(bucketName, objectName, fileStream);
                model.addAttribute("message", "Файл " + file.getOriginalFilename() + " успешно загружен.");
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("message", "Ошибка загрузки файла " + file.getOriginalFilename() + ": " + e.getMessage());
            }
        }
        return "redirect:/";
    }
}

