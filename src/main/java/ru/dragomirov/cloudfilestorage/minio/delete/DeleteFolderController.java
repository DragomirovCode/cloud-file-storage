package ru.dragomirov.cloudfilestorage.minio.delete;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

@Controller
@RequiredArgsConstructor
public class DeleteFolderController {
    private final DeleteFolderService deleteFolderService;
    private final PathUtil pathUtil;

    @DeleteMapping("/delete-folder")
    public String post(
            @RequestParam(name = "path", required = false) String path,
            Authentication authentication
    ) {

        path = pathUtil.clearPath(path);

        String username = authentication.getName();
        String bucketNameHome = "user-" + username;

        deleteFolderService.deleteFolder(bucketNameHome, path);

        path = pathUtil.getPathWithoutLastElement(path);

        return "redirect:/?bucketName=" + bucketNameHome + "&path=" + path;
    }
}
