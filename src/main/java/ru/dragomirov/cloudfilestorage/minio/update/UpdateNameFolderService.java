package ru.dragomirov.cloudfilestorage.minio.update;

import io.minio.*;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateNameFolderService {
    private final MinioClient minioClient;

    @Autowired
    public UpdateNameFolderService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @SneakyThrows
    public void updateNameFolder(String bucketName, String oldFolderName, String newFolderName, String pathFile) {
        List<Item> items = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(oldFolderName)
                        .recursive(true)
                        .build()
        );

        for (Result<Item> result : results) {
            items.add(result.get());
        }

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
