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
                             @RequestParam("objectName") String objectName) {
        try {
            minioService.deleteFile(bucketName, objectName);
            return "redirect:/?bucketName=" + bucketName;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
