package ru.dragomirov.cloudfilestorage.minio.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DeleteFolderController {
    private final DeleteFolderService deleteFolderService;

    @Autowired
    public DeleteFolderController(DeleteFolderService deleteFolderService) {
        this.deleteFolderService = deleteFolderService;
    }


    @DeleteMapping("/delete-bucket")
    public String post(
            @RequestParam("bucketName") String bucketName,
            @RequestParam(name = "path", required = false) String path
    ) {

        if (path == null) {
            path = "";
        } else {
            path = path.trim();
            if (path.startsWith("[") && path.endsWith("]")) {
                path = path.substring(1, path.length() - 1);
            }
        }

        if (!path.isEmpty() && !path.endsWith("/")) {
            path += "/";
        }

        path = path.replaceAll("\\s+", "");
        path = path.replace(",", "/");

        deleteFolderService.deleteFolder(bucketName, path);

        return "redirect:/?bucketName=home";
    }
}
