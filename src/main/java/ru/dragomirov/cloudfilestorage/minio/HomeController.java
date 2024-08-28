package ru.dragomirov.cloudfilestorage.minio;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.dragomirov.cloudfilestorage.minio.upload.UploadFileDto;

import java.util.List;

import static ru.dragomirov.cloudfilestorage.minio.Breadcrumbs.getBreadcrumbLinksForPath;
import static ru.dragomirov.cloudfilestorage.minio.Breadcrumbs.getFolderNamesForPath;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final GetListObjectService getListObjectService;
    private final PathUtil pathUtil;

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

        String childPaths = String.join("/", getFolderNamesForPath(path));

        model.addAttribute("isEmptyPath", isEmptyPath);
        model.addAttribute("objects", objectNames);
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("breadcrumbLinks", getBreadcrumbLinksForPath(path));
        model.addAttribute("currentPath", getFolderNamesForPath(path));
        model.addAttribute("childPaths", childPaths);
        model.addAttribute("uploadFileDto", new UploadFileDto());

        return "home";
    }
}
