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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateFileService {
    private final MinioClient minioClient;

    @SneakyThrows
    @Transactional
    public void updateFile(String bucketName, String oldObjectName, String newObjectName, String path) {
        List<String> objectNames = listObjects(bucketName, path).stream()
                .map(Item::objectName)
                .collect(Collectors.toList());

        if (objectNames.contains(newObjectName)) {
            throw new DuplicateItemException("New file name cannot be the same as the old file name");
        }

        copyObject(bucketName, oldObjectName, newObjectName);
        removeObject(bucketName, oldObjectName);
    }

    @SneakyThrows
    private void copyObject(String bucketName, String sourceObjectName, String destinationObjectName) {
        CopySource copySource = CopySource.builder()
                .bucket(bucketName)
                .object(sourceObjectName)
                .build();

        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(bucketName)
                        .object(destinationObjectName)
                        .source(copySource)
                        .build()
        );
    }

    @SneakyThrows
    private void removeObject(String bucketName, String objectName) {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    private List<Item> listObjects(String bucketName, String path) throws Exception {
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
