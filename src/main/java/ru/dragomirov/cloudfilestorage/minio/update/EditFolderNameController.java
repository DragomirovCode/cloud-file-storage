package ru.dragomirov.cloudfilestorage.minio.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.MinioService;

import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class EditFolderNameController {
    private final MinioService minioService;

    @Autowired
    public EditFolderNameController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/pattern-edit-name-folder")
    public String get(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "objectName") String objectName,
            Model model
    ) {
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("objectName", objectName);
        return "edit-folder";
    }

    @PostMapping("/edit-name-folder")
    public String post(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "objectName") String objectName,
            @RequestParam(name = "newObjectName") String newObjectName
    ) {
        String sanitizedObjectName = objectName.endsWith("/") ? objectName.substring(0, objectName.length() - 1) : objectName;
        Path pathFile = Paths.get(sanitizedObjectName);
        Path parent = pathFile.getParent();

        if (parent == null) {
            parent = Paths.get("");
        }


        // Передача параметров в MinioService
        minioService.editFolder(bucketName, objectName, newObjectName, parent.toString());

        return "redirect:/?bucketName=" + bucketName;
    }
}
