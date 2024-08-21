package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            @RequestParam(name = "objectName") String objectName
    ) {
        String homeDir = System.getProperty("user.home");
        String downloadsDir = homeDir + File.separator + "Downloads";

        Path path = Paths.get(objectName);
        String endFileName = path.getFileName().toString();

        String destinationFilePath = downloadsDir + File.separator + endFileName;

        minioService.downloadFile(bucketName, objectName, destinationFilePath);
        return "redirect:/?bucketName=" + bucketName;
    }
}
