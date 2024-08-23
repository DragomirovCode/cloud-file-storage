package ru.dragomirov.cloudfilestorage.minio.download;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.MinioService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class DownloadFileController {
    private final MinioService minioService;

    @Autowired
    public DownloadFileController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/download-file")
    String get(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "objectName") String objectName,
            @RequestParam(name = "path") String path
    ) {

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

        String homeDir = System.getProperty("user.home");
        String downloadsDir = homeDir + File.separator + "Downloads";

        Path pathFile = Paths.get(objectName);
        String endFileName = pathFile.getFileName().toString();

        String destinationFilePath = downloadsDir + File.separator + endFileName;

        minioService.downloadFile(bucketName, objectName, destinationFilePath);
        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
