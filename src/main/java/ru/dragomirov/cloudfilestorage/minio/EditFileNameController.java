package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class EditFileNameController {
    private final MinioService minioService;

    @Autowired
    public EditFileNameController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/pattern-edit-name-file")
    public String get(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "objectName") String objectName,
            Model model
    ) {
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("objectName", objectName);
        return "edit-file";
    }

    @PostMapping("/edit-name-file")
    public String post(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "objectName") String oldObjectName,
            @RequestParam(name = "newObjectName") String newObjectName
    ) {

        Path pathFile = Paths.get(oldObjectName);

        String allNewObjectName;
        Path parentPath = pathFile.getParent();

        if (parentPath != null) {
            String pathBeforeFileName = parentPath.toString();
            pathBeforeFileName = pathBeforeFileName.replace("\\", "/");
            System.out.println(pathBeforeFileName + " pathBeforeFileName");
            allNewObjectName = pathBeforeFileName + "/" + newObjectName;
        } else {
            allNewObjectName = newObjectName;
        }

        minioService.editFile(bucketName, oldObjectName, allNewObjectName);
        return "redirect:/?bucketName=" + bucketName;
    }

}
