package ru.dragomirov.cloudfilestorage.minio.create;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class CreateFolderService {
    private final MinioClient minioClient;

    @Autowired
    public CreateFolderService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @SneakyThrows
    public void createFolder(String bucketName, String folderName, String path) {
        InputStream keepFileStream = new ByteArrayInputStream(new byte[0]);
        String objectName = path + "/" + folderName + "/.keep";

        System.out.println("path: " + path);

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(keepFileStream, 0, -1)
                        .build()
        );
    }
}
