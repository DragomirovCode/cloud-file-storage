package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DeleteFileController {
    private final MinioService minioService;

    @Autowired
    public DeleteFileController(MinioService minioService) {
        this.minioService = minioService;
    }

    @DeleteMapping("/delete-file")
    public String deleteFile(@RequestParam("bucketName") String bucketName,
                             @RequestParam(name = "path", required = false) String path,
                             @RequestParam("objectName") String objectName) {
        try {
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

            path = path.replaceAll("\\s+", "");
            path = path.replace(",", "/");

            if (!path.isEmpty() && !path.endsWith("/")) {
                path += "/";
            }

            minioService.deleteFile(bucketName, objectName);
            return "redirect:/?bucketName=" + bucketName + "&path=" + path;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
