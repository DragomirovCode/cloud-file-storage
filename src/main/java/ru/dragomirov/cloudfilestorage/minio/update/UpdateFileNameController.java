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
public class UpdateFileNameController {
    private final UpdateFileService updateFileService;
    private final FileUtil fileUtil;
    private final PathUtil pathUtil;

    @GetMapping("/pattern-update-name-file")
    public String get(
            @RequestParam(name = "path") String path,
            @RequestParam(name = "objectName") String objectName,
            Model model
    ) {
        model.addAttribute("objectName", objectName);
        model.addAttribute("childPaths", path);
        model.addAttribute("updateFileDto", new UpdateFileDto());
        return "minio/update-file";
    }

    @PostMapping("/update-name-file")
    public String post(
            @RequestParam(name = "objectName") String oldObjectName,
            @Valid @ModelAttribute("updateFileDto") UpdateFileDto updateFileDto,
            BindingResult bindingResult,
            @RequestParam(name = "path", required = false) String path,
            Model model,
            Authentication authentication
    ) {

        String username = authentication.getName();
        String bucketNameHome = "user-" + username;

        if (bindingResult.hasErrors()) {
            model.addAttribute("bucketName", bucketNameHome);
            model.addAttribute("objectName", oldObjectName);
            model.addAttribute("childPaths", path);
            return "minio/update-file";
        }

        path = pathUtil.clearPath(path);

        String allNewObjectName = fileUtil.generateNewFileNameWithExtension(oldObjectName, updateFileDto.file);

        updateFileService.updateFile(bucketNameHome, oldObjectName, allNewObjectName, path);

        return "redirect:/?path=" + path;
    }
}
