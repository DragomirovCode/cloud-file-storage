package ru.dragomirov.cloudfilestorage.minio.update;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dragomirov.cloudfilestorage.minio.exception.DuplicateItemException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateFolderService {
    private final MinioClient minioClient;

    @SneakyThrows
    @Transactional
    public void updateNameFolder(String bucketName, String oldFolderName, String newFolderName, String pathFile) {
        List<String> objectNames = listObjects(bucketName, pathFile).stream()
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

    @SneakyThrows
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
            items.add(result.get());
        }
        return items;
    }

    @SneakyThrows
    private void copyObjectsToNewFolder(String bucketName, List<Item> items, String oldFolderName, String newFolderName, String pathFile) {
        for (Item item : items) {
            String oldObjectName = item.objectName();
            if (oldObjectName.startsWith(oldFolderName)) {
                String relativePath = oldObjectName.substring(oldFolderName.length());
                String newObjectName = pathFile + "/" + newFolderName + "/" + relativePath;

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(bucketName)
                                .object(newObjectName)
                                .source(CopySource.builder().bucket(bucketName).object(oldObjectName).build())
                                .build()
                );
            }
        }
    }

    @SneakyThrows
    private void removeOldObjects(String bucketName, List<Item> items, String oldFolderName) {
        for (Item item : items) {
            String oldObjectName = item.objectName();
            if (oldObjectName.startsWith(oldFolderName)) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder().bucket(bucketName).object(oldObjectName).build()
                );
            }
        }
    }

    private void validateFolderName(String oldObjectName, String newObjectName) {
        if (Objects.equals(oldObjectName, newObjectName)) {
            throw new DuplicateItemException("New folder name cannot be the same as the old folder name");
        }
    }

    @SneakyThrows
    private List<Item> listObjects(String bucketName, String path){
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(path)
                        .delimiter("/")
                        .build()
        );

        List<Item> objects = new ArrayList<>();
        for (Result<Item> result : results) {
            objects.add(result.get());
        }
        return objects;
    }
}
