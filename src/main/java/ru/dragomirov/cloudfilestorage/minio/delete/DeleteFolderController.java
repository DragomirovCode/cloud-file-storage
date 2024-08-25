package ru.dragomirov.cloudfilestorage.minio.delete;

import lombok.RequiredArgsConstructor;
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
            @RequestParam("bucketName") String bucketName,
            @RequestParam(name = "path", required = false) String path
    ) {

        path = pathUtil.clearPath(path);

        deleteFolderService.deleteFolder(bucketName, path);

        path = pathUtil.getPathWithoutLastElement(path);

        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
