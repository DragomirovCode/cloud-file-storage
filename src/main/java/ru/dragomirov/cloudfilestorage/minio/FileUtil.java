package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.dragomirov.cloudfilestorage.minio.exception.DuplicateItemException;
import ru.dragomirov.cloudfilestorage.minio.exception.InvalidParameterException;

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

    public void validateFileName(MultipartFile[] files) {
        for (MultipartFile file : files) {
            String fileOriginalFilename = file.getOriginalFilename();

            assert fileOriginalFilename != null;
            if (fileOriginalFilename.isEmpty()) {
                throw new InvalidParameterException("The file cannot be empty");
            }

            String fileName = fileNameWithoutExtension(fileOriginalFilename);

            if (fileName.length() < 3) {
                throw new InvalidParameterException("File name must be at least 3 characters long");
            }

            if (!fileOriginalFilename.contains(".") || fileOriginalFilename.lastIndexOf('.') == 0) {
                throw new InvalidParameterException("File must have a valid extension");
            }

            if (!fileName.matches("^[a-zA-Z0-9-]+$")) {
                throw new InvalidParameterException("File name must contain only English letters, digits, and hyphens");
            }
        }
    }

    public void validateFolderName(MultipartFile[] files) {
        for (MultipartFile file : files) {
            String fileOriginalFilename = file.getOriginalFilename();

            assert fileOriginalFilename != null;
            if (fileOriginalFilename.isEmpty()) {
                throw new InvalidParameterException("The folder cannot be empty");
            }

            String folderName = folderName(fileOriginalFilename);

            if (folderName.length() < 3) {
                throw new InvalidParameterException("Folder name must be at least 3 characters long");
            }

            if (!folderName.matches("^[a-zA-Z0-9-]+$")) {
                throw new InvalidParameterException("Folder name must contain only English letters, digits, and hyphens");
            }
        }
    }

    private String fileNameWithoutExtension(String fileOriginalFilename) {
        Path path = Paths.get(fileOriginalFilename);
        String fileName = path.getFileName().toString();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public String folderName(String folderName) {
        if (folderName.equals("/")) {
            return "";
        }

        Path path = Paths.get(folderName);
        Path parentPath = path.getParent();

        if (parentPath == null) {
            return path.getFileName().toString();
        }

        return parentPath.getFileName().toString();
    }

}

