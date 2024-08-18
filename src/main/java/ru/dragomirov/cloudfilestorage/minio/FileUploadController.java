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
    public String uploadFiles(@RequestParam(name = "bucketName") String bucketName,
                              @RequestParam(name = "path", required = false) String path,
                              @RequestParam(name = "files") MultipartFile[] files, Model model) {

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

        path = path.replaceAll("\\s+", "");
        path = path.replace(",", "/");

        for (MultipartFile file : files) {
            try {
                InputStream fileStream = file.getInputStream();

                // Формирование имени объекта
                String objectName = path + file.getOriginalFilename();

                // Убедитесь, что объектное имя корректное
                System.out.println("Uploading to: " + objectName);

                minioService.uploadFile(bucketName, objectName, fileStream);
                model.addAttribute("message", "Файл " + file.getOriginalFilename() + " успешно загружен.");
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("message", "Ошибка загрузки файла " + file.getOriginalFilename() + ": " + e.getMessage());
            }
        }
        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}

