package ru.dragomirov.cloudfilestorage.minio.delete;

import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

@Controller
public class DeleteFileController {
    private final DeleteFileService deleteFileService;
    private final PathUtil pathUtil;

    @Autowired
    public DeleteFileController(DeleteFileService deleteFileService, PathUtil pathUtil) {
        this.deleteFileService = deleteFileService;
        this.pathUtil = pathUtil;
    }

    @SneakyThrows
    @DeleteMapping("/delete-file")
    public String deleteFile(@RequestParam("bucketName") String bucketName,
                             @RequestParam(name = "path", required = false) String path,
                             @RequestParam("objectName") String objectName) {

        path = pathUtil.clearPath(path);

        deleteFileService.deleteFile(bucketName, objectName);
        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
