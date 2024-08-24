package ru.dragomirov.cloudfilestorage.minio.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

@Controller
public class DeleteFolderController {
    private final DeleteFolderService deleteFolderService;
    private final PathUtil pathUtil;

    @Autowired
    public DeleteFolderController(DeleteFolderService deleteFolderService, PathUtil pathUtil) {
        this.deleteFolderService = deleteFolderService;
        this.pathUtil = pathUtil;
    }


    @DeleteMapping("/delete-bucket")
    public String post(
            @RequestParam("bucketName") String bucketName,
            @RequestParam(name = "path", required = false) String path
    ) {

        path = pathUtil.clearPath(path);

        deleteFolderService.deleteFolder(bucketName, path);

        path = String.valueOf(pathUtil.getPathWithoutLastElement(path));

        return "redirect:/?bucketName=" + bucketName + "&path=" + path;
    }
}
