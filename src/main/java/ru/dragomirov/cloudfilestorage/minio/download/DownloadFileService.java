package ru.dragomirov.cloudfilestorage.minio.download;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.InputStream;

@Service
public class DownloadFileService {
    private final MinioClient minioClient;

    @Autowired
    public DownloadFileService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @SneakyThrows
    public void downloadFile(String bucketName, String objectName, String destinationFilePath) {
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());

        FileOutputStream fos = new FileOutputStream(destinationFilePath);
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
    }
}
