package ru.dragomirov.cloudfilestorage.minio.create;

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

import jakarta.validation.Valid;

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
        model.addAttribute("minioDto", new CreateFolderDto());
        return "create-folder";
    }

    @PostMapping("/create-new-folder")
    public String post(
            @Valid @ModelAttribute("minioDto") CreateFolderDto createFolderDto,
            BindingResult bindingResult,
            @RequestParam(name = "path", required = false) String path,
            Authentication authentication
    ) {
        if (bindingResult.hasErrors()) {
            return "create-folder";
        }

        path = pathUtil.clearPath(path);

        String username = authentication.getName();
        String bucketNameHome = "user-" + username;

        createFolderService.createFolder(bucketNameHome, createFolderDto.folder, path);
        return "redirect:/?path=" + path;
    }
}
