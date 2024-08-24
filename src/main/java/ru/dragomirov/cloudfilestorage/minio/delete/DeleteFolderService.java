package ru.dragomirov.cloudfilestorage.minio.delete;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeleteFolderService {
    private final MinioClient minioClient;

    @Autowired
    public DeleteFolderService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void deleteFolder(String bucketName, String folderPrefix) {
        List<String> objectsToDelete = listObjectsInFolder(bucketName, folderPrefix);
        if (!objectsToDelete.isEmpty()) {
            deleteObjects(bucketName, objectsToDelete);
        }
    }

    @SneakyThrows
    private List<String> listObjectsInFolder(String bucketName, String folderPrefix) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(folderPrefix)
                        .recursive(true)
                        .build()
        );

        List<String> objectsToDelete = new ArrayList<>();
        for (Result<Item> result : results) {
            Item item = result.get();
            objectsToDelete.add(item.objectName());
        }
        return objectsToDelete;
    }

    @SneakyThrows
    private void deleteObjects(String bucketName, List<String> objectsToDelete) {
        for (String objectName : objectsToDelete) {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        }
    }
}
