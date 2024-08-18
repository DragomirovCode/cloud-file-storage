package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Controller
public class PackageUploadController {
    private final MinioService minioService;

    @Autowired
    public PackageUploadController(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/package-upload")
    public String uploadPackage(@RequestParam(name = "bucketName", defaultValue = "home") String bucketName,
                                @RequestParam(name = "path", required = false) String path,
                                @RequestParam(name = "package-files") MultipartFile[] files, Model model) {

        // Очистка пути
        if (path == null) {
            path = "";
        } else {
            // Преобразование в строку и удаление пробелов
            path = path.toString().trim();
            if (path.startsWith("[") && path.endsWith("]")) {
                path = path.substring(1, path.length() - 1);  // Удаление квадратных скобок
            }
        }

        if (!path.isEmpty() && !path.endsWith("/")) {
            path += "/";
        }

        for (MultipartFile file : files) {
            try {
                InputStream fileStream = file.getInputStream();

                // Формирование имени объекта
                String objectName = path + file.getOriginalFilename();

                minioService.uploadFile(bucketName, objectName, fileStream);
                model.addAttribute("message", "Файл " + file.getOriginalFilename() + " успешно загружен.");
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("message", "Ошибка загрузки файла " + file.getOriginalFilename() + ": " + e.getMessage());
            }
        }
        return "redirect:/?bucketName=" + bucketName;
    }
}
