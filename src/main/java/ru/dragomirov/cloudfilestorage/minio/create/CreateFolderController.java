package ru.dragomirov.cloudfilestorage.minio.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.MinioService;

@Controller
public class CreateFolderController {
    private final MinioService minioService;

    @Autowired
    public CreateFolderController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/pattern-create-new-folder")
    public String get(
            @RequestParam(name = "path") String path,
            Model model
    ) {
        model.addAttribute("fullPath", path);
        return "create-folder";
    }


    @PostMapping("/create-new-folder")
    public String post(
            @RequestParam(name = "bucketName", defaultValue = "home") String bucketName,
            @RequestParam(name = "folderName") String folderName,
            @RequestParam(name = "path") String path
    ) {
        minioService.createFolder(bucketName, folderName, path);
        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
