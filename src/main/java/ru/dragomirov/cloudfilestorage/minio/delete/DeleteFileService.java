package ru.dragomirov.cloudfilestorage.minio.delete;

import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dragomirov.cloudfilestorage.minio.exception.MinioOperationException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class DeleteFileService {
    private static final System.Logger logger = System.getLogger(DeleteFileService.class.getName());
    private final MinioClient minioClient;

    @Transactional
    public void deleteFile(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            logger.log(System.Logger.Level.ERROR,
                    String.format("Error occurred while deleting file '%s' from bucket '%s'", objectName, bucketName),
                    e);
            throw new MinioOperationException();
        }
    }
}
