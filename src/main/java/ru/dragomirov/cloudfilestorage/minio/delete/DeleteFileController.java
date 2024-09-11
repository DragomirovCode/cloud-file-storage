package ru.dragomirov.cloudfilestorage.minio.delete;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

@Controller
@RequiredArgsConstructor
public class DeleteFileController {
    private final DeleteFileService deleteFileService;
    private final PathUtil pathUtil;

    @DeleteMapping("/delete-file")
    public String delete(
            @RequestParam(name = "path", required = false) String path,
            @RequestParam("objectName") String objectName,
            Authentication authentication
    ) {

        path = pathUtil.clearPath(path);

        String username = authentication.getName();
        String bucketNameHome = "user-" + username;

        deleteFileService.deleteFile(bucketNameHome, objectName);

        return "redirect:/?path=" + path;
    }
}
