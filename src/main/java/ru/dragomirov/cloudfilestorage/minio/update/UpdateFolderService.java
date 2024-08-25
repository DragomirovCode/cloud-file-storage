package ru.dragomirov.cloudfilestorage.minio.update;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateFolderService {
    private final MinioClient minioClient;

    @SneakyThrows
    public void updateNameFolder(String bucketName, String oldFolderName, String newFolderName, String pathFile) {
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
}
