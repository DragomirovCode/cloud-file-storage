package ru.dragomirov.cloudfilestorage.minio.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.dragomirov.cloudfilestorage.minio.FileUtil;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

@Controller
@RequiredArgsConstructor
public class UploadFolderController {
    private final UploadService uploadService;
    private final PathUtil pathUtil;
    private final FileUtil fileUtil;

    @PostMapping("/upload-folder")
    public String post(
            @RequestParam(name = "path", required = false) String path,
            @RequestParam(name = "folder-files") MultipartFile[] files,
            Authentication authentication
    ) {

        path = pathUtil.clearPath(path);

        String username = authentication.getName();
        String bucketNameHome = "user-" + username;

        fileUtil.validateFolderName(files);

        fileUtil.validateFileName(files);

        uploadService.uploadMultipleFiles(files, path, bucketNameHome);

        return "redirect:/?path=" + path;
    }
}
