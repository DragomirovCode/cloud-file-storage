package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileUtil {
    public String generateNewFileNameWithExtension(String oldObjectName, String newObjectName) {
        Path pathFile = Paths.get(oldObjectName);
        String fileExtension = extractFileExtension(pathFile);
        String newFileNameWithExtension = newObjectName + fileExtension;
        return buildFullPath(pathFile.getParent(), newFileNameWithExtension);
    }

    private String extractFileExtension(Path pathFile) {
        String fileName = pathFile.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex != -1) ? fileName.substring(dotIndex) : "";
    }

    private String buildFullPath(Path parentPath, String newFileNameWithExtension) {
        if (parentPath != null) {
            String pathBeforeFileName = parentPath.toString().replace("\\", "/");
            return pathBeforeFileName + "/" + newFileNameWithExtension;
        } else {
            return newFileNameWithExtension;
        }
    }
}

