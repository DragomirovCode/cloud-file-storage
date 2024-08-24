package ru.dragomirov.cloudfilestorage.minio.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.FileUtil;

@Controller
public class UpdateFileNameController {
    private final UpdateFileService updateFileService;
    private final FileUtil fileUtil;

    @Autowired
    public UpdateFileNameController(UpdateFileService updateFileService, FileUtil fileUtil) {
        this.updateFileService = updateFileService;
        this.fileUtil = fileUtil;
    }

    @GetMapping("/pattern-edit-name-file")
    public String get(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "path") String path,
            @RequestParam(name = "objectName") String objectName,
            Model model
    ) {
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("objectName", objectName);
        model.addAttribute("childPaths", path);
        return "edit-file";
    }

    @PostMapping("/edit-name-file")
    public String post(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "objectName") String oldObjectName,
            @RequestParam(name = "newObjectName") String newObjectName,
            @RequestParam(name = "path") String path
    ) {
        String allNewObjectName = fileUtil.generateNewFileNameWithExtension(oldObjectName, newObjectName);

        updateFileService.updateFile(bucketName, oldObjectName, allNewObjectName);
        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
