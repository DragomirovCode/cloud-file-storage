package ru.dragomirov.cloudfilestorage.minio.delete;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

@Controller
@RequiredArgsConstructor
public class DeleteFileController {
    private final DeleteFileService deleteFileService;
    private final PathUtil pathUtil;

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
