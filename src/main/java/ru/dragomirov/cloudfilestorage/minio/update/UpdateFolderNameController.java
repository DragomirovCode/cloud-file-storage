package ru.dragomirov.cloudfilestorage.minio.update;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "path") String path,
            @RequestParam(name = "objectName") String objectName,
            Model model
    ) {
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("objectName", objectName);
        model.addAttribute("childPaths", path);
        model.addAttribute("updateFolderDto", new UpdateFolderDto());
        return "update-folder";
    }

    @PostMapping("/update-name-folder")
    // FIXME: 30.08.2024 the problem with naming is when the path is long
    public String post(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "objectName") String objectName,
            @Valid @ModelAttribute("updateFolderDto") UpdateFolderDto updateFolderDto,
            BindingResult bindingResult,
            @RequestParam(name = "path") String path,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bucketName", bucketName);
            model.addAttribute("objectName", objectName);
            model.addAttribute("childPaths", path);
            return "update-folder";
        }

        path = pathUtil.clearPath(path);

        String parent = pathUtil.getParentPathSafe(objectName);

        updateFolderService.updateNameFolder(bucketName, objectName, updateFolderDto.folder, parent);

        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
