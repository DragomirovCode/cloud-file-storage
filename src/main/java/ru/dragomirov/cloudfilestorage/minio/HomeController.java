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
    public String getListObjects(@RequestParam(name = "bucketName", defaultValue = "home") String bucketName,
                                 @RequestParam(name = "path", required = false) String path,
                                 Model model) {
        try {
            if (path == null) {
                path = "";
            }

            if (!path.isEmpty() && !path.endsWith("/")) {
                path += "/";
            }

            List<String> objectNames = minioService.listObjects(bucketName, path).stream()
                    .map(Item::objectName)  // Преобразуем объекты в строки с именами файлов/папок
                    .toList();

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
