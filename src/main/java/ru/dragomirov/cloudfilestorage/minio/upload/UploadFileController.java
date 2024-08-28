package ru.dragomirov.cloudfilestorage.minio.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;
import ru.dragomirov.cloudfilestorage.minio.exception.InvalidParameterException;

@Controller
@RequiredArgsConstructor
public class UploadFileController {
    private final UploadService uploadService;
    private final PathUtil pathUtil;

    @PostMapping("/upload-files")
    public String post(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "path", required = false) String path,
            @ModelAttribute("files") UploadFileDto uploadFileDto
    ) {

        MultipartFile[] files = uploadFileDto.getFiles();
        if (files == null || files.length == 0) {
            throw new InvalidParameterException("You must select at least one file");
        } else {
            for (MultipartFile file : files) {
                String filename = file.getOriginalFilename();
                if (filename == null || filename.length() < 3) {
                    throw new InvalidParameterException("File name must be at least 3 characters long");
                }
                if (!filename.matches("^[a-zA-Z0-9-]+$")) {
                    throw new InvalidParameterException("File name must contain only English letters, digits, and hyphens");
                }
            }
        }

        path = pathUtil.clearPath(path);

        uploadService.uploadMultipleFiles(uploadFileDto.files, path, bucketName);
        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
