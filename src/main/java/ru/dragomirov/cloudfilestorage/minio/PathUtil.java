package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class PathUtil {
    public String clearPath(String path) {
        if (path == null) {
            return "";
        }

        path = path.replace(",", "/");
        path = path.replace("\\", "/");

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

        endFileName = endFileName.replace("\\", "/");

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
