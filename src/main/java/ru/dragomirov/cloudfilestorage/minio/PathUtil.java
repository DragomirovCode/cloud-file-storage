package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class PathUtil {
    public String clearPath(String path) {
        if (path == null) {
            return "";
        }

        try {
            path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        path = path.trim();
        if (path.startsWith("[") && path.endsWith("]")) {
            path = path.substring(1, path.length() - 1);
        }

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

    public String getPathWithoutLastElement(String path) {
        Path pathFile = Paths.get(path);
        Path subPath;
        String pathBeforeFileName;
        if (pathFile.getNameCount() > 1) {
            subPath = pathFile.subpath(0, pathFile.getNameCount() - 1);
            pathBeforeFileName = subPath.toString();
            pathBeforeFileName = pathBeforeFileName.replace("\\", "/");
        } else {
            subPath = Path.of("");
            pathBeforeFileName = subPath.toString();
        }
        return pathBeforeFileName;
    }

    public String getDownloadsFilePath(String objectName) {
        String homeDir = System.getProperty("user.home");
        String downloadsDir = homeDir + File.separator + "Downloads";

        Path pathFile = Paths.get(objectName);
        String endFileName = pathFile.getFileName().toString();

        return downloadsDir + File.separator + endFileName;
    }

    public String getParentPathSafe(String objectName) {
        String sanitizedObjectName = objectName.endsWith("/") ? objectName.substring(0, objectName.length() - 1) : objectName;
        Path pathFile = Paths.get(sanitizedObjectName);
        Path parent = pathFile.getParent();

        if (parent == null) {
            parent = Paths.get("");
        }

        String resultPath = String.valueOf(parent);
        resultPath = resultPath.replace("\\", "/");

        return resultPath;
    }
}
