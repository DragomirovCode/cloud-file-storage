package ru.dragomirov.cloudfilestorage.minio.update;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
            @RequestParam(name = "path") String path,
            @RequestParam(name = "objectName") String objectName,
            Model model
    ) {
        model.addAttribute("objectName", objectName);
        model.addAttribute("childPaths", path);
        model.addAttribute("updateFolderDto", new UpdateFolderDto());
        return "minio/update-folder";
    }

    @PostMapping("/update-name-folder")
    public String post(
            @RequestParam(name = "objectName") String objectName,
            @Valid @ModelAttribute("updateFolderDto") UpdateFolderDto updateFolderDto,
            BindingResult bindingResult,
            @RequestParam(name = "path") String path,
            Model model,
            Authentication authentication
    ) {

        String username = authentication.getName();
        String bucketNameHome = "user-" + username;

        if (bindingResult.hasErrors()) {
            model.addAttribute("bucketName", bucketNameHome);
            model.addAttribute("objectName", objectName);
            model.addAttribute("childPaths", path);
            return "minio/update-folder";
        }

        path = pathUtil.clearPath(path);

        String parent = pathUtil.getParentPathSafe(objectName);

        updateFolderService.updateNameFolder(bucketNameHome, objectName, updateFolderDto.folder, parent);

        return "redirect:/?path=" + path;
    }
}
