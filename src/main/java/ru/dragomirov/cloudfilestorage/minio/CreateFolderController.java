package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CreateFolderController {
    private final MinioService minioService;

    @Autowired
    public CreateFolderController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/pattern-create-new-folder")
    public String get() {
        return "create-folder";
    }


    @PostMapping("/create-new-folder")
    public String post(
            @RequestParam(name = "bucketName", defaultValue = "home") String bucketName,
            @RequestParam(name = "folderName") String folderName
    ) {

        minioService.createFolder(bucketName, folderName);

        return "redirect:/?bucketName=" + bucketName;
    }
}
