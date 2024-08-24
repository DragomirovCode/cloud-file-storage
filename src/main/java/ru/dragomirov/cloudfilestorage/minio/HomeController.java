package ru.dragomirov.cloudfilestorage.minio;

import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static ru.dragomirov.cloudfilestorage.minio.Breadcrumbs.getBreadcrumbLinksForPath;
import static ru.dragomirov.cloudfilestorage.minio.Breadcrumbs.getFolderNamesForPath;

@Controller
public class HomeController {
    private final GetListObjectService getListObjectService;
    private final PathUtil pathUtil;

    @Autowired
    public HomeController(GetListObjectService getListObjectService, PathUtil pathUtil) {
        this.getListObjectService = getListObjectService;
        this.pathUtil = pathUtil;
    }

    @SneakyThrows
    @GetMapping("/")
    public String getListObjects(@RequestParam(name = "bucketName") String bucketName,
                                 @RequestParam(name = "path", required = false) String path,
                                 Model model) {

        path = pathUtil.clearPath(path);

        List<String> objectNames = getListObjectService.listObjects(bucketName, path).stream()
                .map(Item::objectName)
                .toList();

        List<String> folderNames = getFolderNamesForPath(path.trim());

        boolean isEmptyPath = pathUtil.isEmptyPath(folderNames);

        String childPaths = pathUtil.clearPath(getFolderNamesForPath(path).toString());

        model.addAttribute("isEmptyPath", isEmptyPath);
        model.addAttribute("objects", objectNames);
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("breadcrumbLinks", getBreadcrumbLinksForPath(path));
        model.addAttribute("currentPath", getFolderNamesForPath(path));
        model.addAttribute("childPaths", childPaths);

        return "home";
    }
}
