package ru.dragomirov.cloudfilestorage.minio.delete;

import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteFileService {
    private final MinioClient minioClient;

    @Autowired
    public DeleteFileService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void deleteFile(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }
}
