package ru.dragomirov.cloudfilestorage.minio.home;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;

import java.util.List;

import static ru.dragomirov.cloudfilestorage.minio.home.BreadcrumbsUtil.getBreadcrumbLinksForPath;
import static ru.dragomirov.cloudfilestorage.minio.home.BreadcrumbsUtil.getFolderNamesForPath;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;
    private final PathUtil pathUtil;

    @GetMapping("/")
    public String get(
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
            objectNames = homeService.getObjectByName(bucketNameHome, path, fileName).stream()
                    .map(Item::objectName)
                    .toList();
        } else {
            objectNames = homeService.getListObjects(bucketNameHome, path).stream()
                    .map(Item::objectName)
                    .toList();
        }

        List<String> folderNames = getFolderNamesForPath(path.trim());

        boolean isEmptyPath = pathUtil.isEmptyPath(folderNames);

        String childPaths = String.join("/", getFolderNamesForPath(path));

        model.addAttribute("isEmptyPath", isEmptyPath);
        model.addAttribute("objects", objectNames);
        model.addAttribute("breadcrumbLinks", getBreadcrumbLinksForPath(path));
        model.addAttribute("currentPath", getFolderNamesForPath(path));
        model.addAttribute("childPaths", childPaths);

        return "minio/home";
    }
}
