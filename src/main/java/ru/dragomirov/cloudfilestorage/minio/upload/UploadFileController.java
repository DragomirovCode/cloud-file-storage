package ru.dragomirov.cloudfilestorage.minio.upload;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

@Controller
@RequiredArgsConstructor
public class UploadFileController {
    private final UploadService uploadService;
    private final PathUtil pathUtil;

    @SneakyThrows
    @PostMapping("/upload-files")
    public String post(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "path", required = false) String path,
            @RequestParam(name = "files") MultipartFile[] files
    ) {

        path = pathUtil.clearPath(path);

        uploadService.uploadMultipleFiles(files, path, bucketName);
        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
