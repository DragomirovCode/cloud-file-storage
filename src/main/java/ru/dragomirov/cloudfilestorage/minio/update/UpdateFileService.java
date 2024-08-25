package ru.dragomirov.cloudfilestorage.minio.update;

import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateFileService {
    private final MinioClient minioClient;

    public void updateFile(String bucketName, String oldObjectName, String newObjectName) {
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
}
