package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        String path = "C:\\Users\\Твой дом\\OneDrive\\Рабочий стол\\new-folder\\Screenshot_1.png";

        minioService.downloadFile(bucketName, objectName, path);
        return "redirect:/?bucketName=" + bucketName;
    }
}
