package ru.dragomirov.cloudfilestorage.minio;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.get.GetFileByNameService;
import ru.dragomirov.cloudfilestorage.minio.get.GetListObjectService;

import java.util.List;

import static ru.dragomirov.cloudfilestorage.minio.Breadcrumbs.getBreadcrumbLinksForPath;
import static ru.dragomirov.cloudfilestorage.minio.Breadcrumbs.getFolderNamesForPath;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final GetListObjectService getListObjectService;
    private final GetFileByNameService getFileByNameService;
    private final PathUtil pathUtil;

    @GetMapping("/")
    public String getListObjects(
            @RequestParam(name = "path", required = false) String path,
            Model model,
            Authentication authentication,
            @RequestParam(name = "fileName", required = false) String fileName
    ) {

        path = pathUtil.clearPath(path);

        String username = authentication.getName();
        String bucketNameHome = "user-" + username;

        List<String> objectNames;

        if (fileName != null && !fileName.isEmpty()) {
            objectNames = getFileByNameService.getObjectByName(bucketNameHome, path, fileName).stream()
                    .map(Item::objectName)
                    .toList();
        } else {
            objectNames = getListObjectService.listObjects(bucketNameHome, path).stream()
                    .map(Item::objectName)
                    .toList();
        }

        System.out.println("objectNames: " + objectNames);;

        List<String> folderNames = getFolderNamesForPath(path.trim());

        boolean isEmptyPath = pathUtil.isEmptyPath(folderNames);

        String childPaths = String.join("/", getFolderNamesForPath(path));

        model.addAttribute("isEmptyPath", isEmptyPath);
        model.addAttribute("objects", objectNames);
        model.addAttribute("breadcrumbLinks", getBreadcrumbLinksForPath(path));
        model.addAttribute("currentPath", getFolderNamesForPath(path));
        model.addAttribute("childPaths", childPaths);

        return "home";
    }
}
