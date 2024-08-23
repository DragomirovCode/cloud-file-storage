package ru.dragomirov.cloudfilestorage.minio.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.MinioService;

@Controller
public class DeleteFolderController {
    private final MinioService minioService;

    @Autowired
    public DeleteFolderController(MinioService minioService) {
        this.minioService = minioService;
    }

    @DeleteMapping("/delete-bucket")
    public String post(
            @RequestParam("bucketName") String bucketName,
            @RequestParam(name = "path", required = false) String path
    ) {

        if (path == null) {
            path = "";
        } else {
            path = path.trim();
            if (path.startsWith("[") && path.endsWith("]")) {
                path = path.substring(1, path.length() - 1);
            }
        }

        if (!path.isEmpty() && !path.endsWith("/")) {
            path += "/";
        }

        path = path.replaceAll("\\s+", "");
        path = path.replace(",", "/");

        minioService.deleteFolder(bucketName, path);

        return "redirect:/?bucketName=home";
    }
}
