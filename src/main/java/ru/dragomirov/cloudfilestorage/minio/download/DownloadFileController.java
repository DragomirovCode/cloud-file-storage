package ru.dragomirov.cloudfilestorage.minio.download;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

@Controller
public class DownloadFileController {
    private final DownloadFileService downloadFileService;
    private final PathUtil pathUtil;

    @Autowired
    public DownloadFileController(DownloadFileService downloadFileService, PathUtil pathUtil) {
        this.downloadFileService = downloadFileService;
        this.pathUtil = pathUtil;
    }

    @SneakyThrows
    @GetMapping("/download-file")
    String get(
            @RequestParam(name = "bucketName") String bucketName,
            @RequestParam(name = "objectName") String objectName,
            @RequestParam(name = "path") String path
    ) {
        path = pathUtil.clearPath(path);

        String destinationFilePath = pathUtil.getDownloadsFilePath(objectName);

        downloadFileService.downloadFile(bucketName, objectName, destinationFilePath);
        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
