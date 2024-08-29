package ru.dragomirov.cloudfilestorage.minio.upload;

import lombok.RequiredArgsConstructor;
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
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "path", required = false) String path,
            @ModelAttribute("files") MultipartFile[] files
    ) {

        path = pathUtil.clearPath(path);

        fileUtil.validateFileName(files);

        uploadService.uploadMultipleFiles(files, path, bucketName);

        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
