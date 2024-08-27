package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.stereotype.Component;
import ru.dragomirov.cloudfilestorage.minio.exception.DuplicateItemException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Component
public class FileUtil {
    public String generateNewFileNameWithExtension(String oldObjectName, String newObjectName) {
        Path pathFile = Paths.get(oldObjectName);
        String fileExtension = extractFileExtension(pathFile);
        String newFileNameWithExtension = newObjectName + fileExtension;
        validateFileName(oldObjectName, newFileNameWithExtension);
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

    private void validateFileName(String oldObjectName, String newObjectName) {
        if (Objects.equals(oldObjectName, newObjectName)) {
            throw new DuplicateItemException("New file name cannot be the same as the old file name");
        }
    }
}

