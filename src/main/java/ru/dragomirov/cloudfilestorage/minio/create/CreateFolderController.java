package ru.dragomirov.cloudfilestorage.minio.create;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

@Controller
@RequiredArgsConstructor
public class CreateFolderController {
    private final CreateFolderService createFolderService;
    private final PathUtil pathUtil;

    @GetMapping("/pattern-create-new-folder")
    public String get(
            @RequestParam(name = "path") String path,
            Model model
    ) {
        model.addAttribute("childPaths", path);
        return "create-folder";
    }


    @PostMapping("/create-new-folder")
    public String post(
            @RequestParam(name = "bucketName", defaultValue = "home") String bucketName,
            @RequestParam(name = "folderName") String folderName,
            @RequestParam(name = "path") String path
    ) {
        path = pathUtil.clearPath(path);
        createFolderService.createFolder(bucketName, folderName, path);
        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
