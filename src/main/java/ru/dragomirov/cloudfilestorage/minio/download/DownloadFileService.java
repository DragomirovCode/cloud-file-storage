package ru.dragomirov.cloudfilestorage.minio.download;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dragomirov.cloudfilestorage.minio.exception.MinioOperationException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class DownloadFileService {
    private static final System.Logger logger = System.getLogger(DownloadFileService.class.getName());
    private final MinioClient minioClient;

    private InputStream getFileStream(String bucketName, String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            logger.log(System.Logger.Level.ERROR, "Error occurred while fetching file stream from Minio", e);
            throw new MinioOperationException();
        }
    }

    private void saveFileFromStream(InputStream stream, String destinationFilePath) {
        try (FileOutputStream fos = new FileOutputStream(destinationFilePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            logger.log(System.Logger.Level.ERROR, "Error occurred while saving file to path: " + destinationFilePath, e);
            throw new MinioOperationException();
        }
    }

    @Transactional
    public void downloadFile(String bucketName, String objectName, String destinationFilePath) {
        try (InputStream stream = getFileStream(bucketName, objectName)) {
            saveFileFromStream(stream, destinationFilePath);
        } catch (IOException e) {
            logger.log(System.Logger.Level.ERROR, "Error occurred while handling the file stream", e);
            throw new MinioOperationException();
        }
    }
}
