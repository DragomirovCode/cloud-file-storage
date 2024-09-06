package ru.dragomirov.cloudfilestorage.minio.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.dragomirov.cloudfilestorage.minio.FileUtil;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

@Controller
@RequiredArgsConstructor
public class UploadFileController {
    private final UploadService uploadService;
    private final PathUtil pathUtil;
    private final FileUtil fileUtil;

    @PostMapping("/upload-files")
    public String post(
            @RequestParam(name = "path", required = false) String path,
            @ModelAttribute("files") MultipartFile[] files,
            Authentication authentication
    ) {

        path = pathUtil.clearPath(path);

        String username = authentication.getName();
        String bucketNameHome = "user-" + username;

        fileUtil.validateFileName(files);

        uploadService.uploadMultipleFiles(files, path, bucketNameHome);

        return "redirect:/?bucketName=" + bucketNameHome + "&path=" + path;
    }
}
