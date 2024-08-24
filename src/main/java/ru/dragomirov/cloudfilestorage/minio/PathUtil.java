package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PathUtil {
    public String clearPath(String path) {
        if (path == null) {
            return "";
        } else {
            path = path.trim();
            if (path.startsWith("[") && path.endsWith("]")) {
                path = path.substring(1, path.length() - 1);
            }
        }

        path = path.replaceAll("\\s+", "");
        path = path.replace(",", "/");

        if (!path.isEmpty() && !path.endsWith("/")) {
            path += "/";
        }

        return path;
    }

    public boolean isEmptyPath(List<String> folderNames) {
        return folderNames.isEmpty() ||
                (folderNames.size() == 1 &&
                        (folderNames.get(0).trim().isEmpty() || folderNames.get(0).equals("/")));
    }
}
