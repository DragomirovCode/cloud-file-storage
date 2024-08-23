package ru.dragomirov.cloudfilestorage.minio.update;

import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateFileService {
    private final MinioClient minioClient;

    @Autowired
    public UpdateFileService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @SneakyThrows
    public void updateFile(String bucketName, String oldObjectName, String newObjectName) {
        CopySource copySource = CopySource.builder()
                .bucket(bucketName)
                .object(oldObjectName)
                .build();

        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(bucketName)
                        .object(newObjectName)
                        .source(copySource)
                        .build()
        );

        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(oldObjectName)
                        .build()
        );
    }
}
