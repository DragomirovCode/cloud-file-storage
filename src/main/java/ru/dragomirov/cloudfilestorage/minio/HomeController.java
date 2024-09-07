package ru.dragomirov.cloudfilestorage.minio;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static ru.dragomirov.cloudfilestorage.minio.Breadcrumbs.getBreadcrumbLinksForPath;
import static ru.dragomirov.cloudfilestorage.minio.Breadcrumbs.getFolderNamesForPath;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final GetListObjectService getListObjectService;
    private final PathUtil pathUtil;

    @GetMapping("/")
    public String getListObjects(
            @RequestParam(name = "path", required = false) String path,
            Model model,
            Authentication authentication
    ) {

        path = pathUtil.clearPath(path);

        String username = authentication.getName();
        String bucketNameHome = "user-" + username;

        List<String> objectNames = getListObjectService.listObjects(bucketNameHome, path).stream()
                .map(Item::objectName)
                .toList();

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
