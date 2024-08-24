package ru.dragomirov.cloudfilestorage.minio.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

@Controller
public class UploadFolderController {
    private final UploadService uploadService;
    private final PathUtil pathUtil;

    @Autowired
    public UploadFolderController(UploadService uploadService, PathUtil pathUtil) {
        this.uploadService = uploadService;
        this.pathUtil = pathUtil;
    }

    @PostMapping("/package-upload")
    public String uploadPackage(@RequestParam(name = "bucketName", defaultValue = "home") String bucketName,
                                @RequestParam(name = "path", required = false) String path,
                                @RequestParam(name = "package-files") MultipartFile[] files) {

        path = pathUtil.clearPath(path);

        uploadService.uploadMultipleFiles(files, path, bucketName);
        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
