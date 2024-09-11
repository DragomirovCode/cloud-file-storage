package ru.dragomirov.cloudfilestorage.minio.update;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dragomirov.cloudfilestorage.minio.exception.DuplicateItemException;
import ru.dragomirov.cloudfilestorage.minio.exception.MinioOperationException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateFolderService {
    private static final System.Logger logger = System.getLogger(UpdateFileService.class.getName());
    private final MinioClient minioClient;

    @Transactional
    public void updateNameFolder(String bucketName, String oldFolderName, String newFolderName, String pathFile) {
        List<String> objectNames = getListObjects(bucketName, pathFile).stream()
                .map(Item::objectName)
                .collect(Collectors.toList());

        if (objectNames.contains(newFolderName + "/")) {
            throw new DuplicateItemException("A folder with the same name already exists in the specified path");
        }

        validateFolderName(oldFolderName, newFolderName + "/");
        List<Item> items = getObjectsInFolder(bucketName, oldFolderName);
        copyObjectsToNewFolder(bucketName, items, oldFolderName, newFolderName, pathFile);
        removeOldObjects(bucketName, items, oldFolderName);
    }

    private List<Item> getObjectsInFolder(String bucketName, String folderName) {
        List<Item> items = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(folderName)
                        .recursive(true)
                        .build()
        );

        for (Result<Item> result : results) {
            try {
                items.add(result.get());
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                logger.log(System.Logger.Level.ERROR,
                        String.format("Error occurred while listing objects in folder '%s' in bucket '%s'", folderName, bucketName),
                        e);
                throw new MinioOperationException();
            }
        }
        return items;
    }

    private void copyObjectsToNewFolder(String bucketName, List<Item> items, String oldFolderName, String newFolderName, String pathFile) {
        for (Item item : items) {
            String oldObjectName = item.objectName();
            if (oldObjectName.startsWith(oldFolderName)) {
                String relativePath = oldObjectName.substring(oldFolderName.length());
                String newObjectName = pathFile + "/" + newFolderName + "/" + relativePath;

                try {
                    minioClient.copyObject(
                            CopyObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(newObjectName)
                                    .source(CopySource.builder().bucket(bucketName).object(oldObjectName).build())
                                    .build()
                    );
                } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                         InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                         XmlParserException e) {
                    logger.log(System.Logger.Level.ERROR,
                            String.format("Error occurred while copying object '%s' to '%s' in bucket '%s'", oldObjectName, newObjectName, bucketName),
                            e);
                    throw new MinioOperationException();
                }
            }
        }
    }

    private void removeOldObjects(String bucketName, List<Item> items, String oldFolderName) {
        for (Item item : items) {
            String oldObjectName = item.objectName();
            if (oldObjectName.startsWith(oldFolderName)) {
                try {
                    minioClient.removeObject(
                            RemoveObjectArgs.builder().bucket(bucketName).object(oldObjectName).build()
                    );
                } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                         InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                         XmlParserException e) {
                    logger.log(System.Logger.Level.ERROR,
                            String.format("Error occurred while removing object '%s' from bucket '%s'", oldObjectName, bucketName),
                            e);
                    throw new MinioOperationException();
                }
            }
        }
    }

    private void validateFolderName(String oldObjectName, String newObjectName) {
        if (Objects.equals(oldObjectName, newObjectName)) {
            throw new DuplicateItemException("New folder name cannot be the same as the old folder name");
        }
    }

    private List<Item> getListObjects(String bucketName, String path) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(path)
                        .delimiter("/")
                        .build()
        );

        List<Item> objects = new ArrayList<>();
        for (Result<Item> result : results) {
            try {
                objects.add(result.get());
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                logger.log(System.Logger.Level.ERROR,
                        String.format("Error occurred while listing objects in path '%s' in bucket '%s'", path, bucketName),
                        e);
                throw new MinioOperationException();
            }
        }
        return objects;
    }
}
