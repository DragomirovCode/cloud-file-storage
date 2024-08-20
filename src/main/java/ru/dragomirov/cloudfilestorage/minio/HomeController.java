package ru.dragomirov.cloudfilestorage.minio;

import io.minio.messages.Item;
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
    private final MinioService minioService;

    @Autowired
    public HomeController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/")
    public String getListObjects(@RequestParam(name = "bucketName") String bucketName,
                                 @RequestParam(name = "path", required = false) String path,
                                 Model model) {
        try {
            // Очистка пути
            if (path == null) {
                path = "";
            } else {
                path = path.trim();
                if (path.startsWith("[") && path.endsWith("]")) {
                    path = path.substring(1, path.length() - 1);  // Удаление квадратных скобок
                }
            }

            if (!path.isEmpty() && !path.endsWith("/")) {
                path += "/";
            }

            List<String> objectNames = minioService.listObjects(bucketName, path).stream()
                    .map(Item::objectName)
                    .toList();

            List<String> folderNames = getFolderNamesForPath(path.trim());

            // Проверка: если список пуст или содержит один элемент, который после обрезания пробелов пуст или равен "/"
            boolean isEmptyPath = folderNames.isEmpty() ||
                    (folderNames.size() == 1 &&
                            (folderNames.get(0).trim().isEmpty() || folderNames.get(0).equals("/")));

            System.out.println("folderNames.size(): " + folderNames.size());
            System.out.println("folderNames.get(0): " + (folderNames.isEmpty() ? "empty" : folderNames.get(0)));
            System.out.println("isEmptyPath: " + isEmptyPath);

            model.addAttribute("isEmptyPath", isEmptyPath);
            model.addAttribute("objects", objectNames);
            model.addAttribute("bucketName", bucketName);
            model.addAttribute("breadcrumbLinks", getBreadcrumbLinksForPath(path));
            model.addAttribute("currentPath", getFolderNamesForPath(path));

            return "home";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Ошибка получения списка объектов: " + e.getMessage());
            return "error";
        }
    }



}
