package ru.dragomirov.cloudfilestorage.minio.download;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class DownloadFileService {
    private final MinioClient minioClient;

    @SneakyThrows
    private InputStream getFileStream(String bucketName, String objectName) {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }

    @SneakyThrows
    private void saveFileFromStream(InputStream stream, String destinationFilePath) {
        try (FileOutputStream fos = new FileOutputStream(destinationFilePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    @Transactional
    public void downloadFile(String bucketName, String objectName, String destinationFilePath) throws IOException {
        try (InputStream stream = getFileStream(bucketName, objectName)) {
            saveFileFromStream(stream, destinationFilePath);
        }
    }
}
