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
import ru.dragomirov.cloudfilestorage.minio.FileUtil;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

@Controller
@RequiredArgsConstructor
public class UpdateFileNameController {
    private final UpdateFileService updateFileService;
    private final FileUtil fileUtil;
    private final PathUtil pathUtil;

    @GetMapping("/pattern-update-name-file")
    public String get(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "path") String path,
            @RequestParam(name = "objectName") String objectName,
            Model model
    ) {
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("objectName", objectName);
        model.addAttribute("childPaths", path);
        model.addAttribute("minioDto", new UpdateFileDto());
        return "update-file";
    }

    @PostMapping("/update-name-file")
    public String post(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "objectName") String oldObjectName,
            @Valid @ModelAttribute("minioDto") UpdateFileDto updateFileDto,
            BindingResult bindingResult,
            @RequestParam(name = "path") String path
    ) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "update-file";
        }

        path = pathUtil.clearPath(path);

        String allNewObjectName = fileUtil.generateNewFileNameWithExtension(oldObjectName, updateFileDto.getFile());
        updateFileService.updateFile(bucketName, oldObjectName, allNewObjectName);

        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
