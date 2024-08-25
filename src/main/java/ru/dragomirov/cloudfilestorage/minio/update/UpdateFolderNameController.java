package ru.dragomirov.cloudfilestorage.minio.update;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

@Controller
@RequiredArgsConstructor
public class UpdateFolderNameController {
    private final UpdateFolderService updateFolderService;
    private final PathUtil pathUtil;

    @GetMapping("/pattern-update-name-folder")
    public String get(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "path") String path,
            @RequestParam(name = "objectName") String objectName,
            Model model
    ) {
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("objectName", objectName);
        model.addAttribute("childPaths", path);
        return "update-folder";
    }

    @PostMapping("/update-name-folder")
    public String post(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "objectName") String objectName,
            @RequestParam(name = "newObjectName") String newObjectName,
            @RequestParam(name = "path") String path
    ) {
        path = pathUtil.clearPath(path);

        String parent = pathUtil.getParentPathSafe(objectName);

        updateFolderService.updateNameFolder(bucketName, objectName, newObjectName, parent);

        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
