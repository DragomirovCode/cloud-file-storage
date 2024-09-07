package ru.dragomirov.cloudfilestorage.minio.download;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

@Controller
@RequiredArgsConstructor
public class DownloadFileController {
    private final DownloadFileService downloadFileService;
    private final PathUtil pathUtil;

    @GetMapping("/download-file")
    String get(
            @RequestParam(name = "objectName") String objectName,
            @RequestParam(name = "path", required = false) String path,
            Authentication authentication
    ) {
        path = pathUtil.clearPath(path);

        String username = authentication.getName();
        String bucketNameHome = "user-" + username;

        String destinationFilePath = pathUtil.getDownloadsFilePath(objectName);

        downloadFileService.downloadFile(bucketNameHome, objectName, destinationFilePath);
        return "redirect:/?path=" + path;
    }
}
