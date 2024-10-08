package ru.dragomirov.cloudfilestorage.minio.delete;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dragomirov.cloudfilestorage.minio.exception.MinioOperationException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeleteFolderService {
    private static final System.Logger logger = System.getLogger(DeleteFileService.class.getName());
    private final MinioClient minioClient;

    @Transactional
    public void deleteFolder(String bucketName, String folderPrefix) {
        List<String> objectsToDelete = getListObjectsInFolder(bucketName, folderPrefix);
        if (!objectsToDelete.isEmpty()) {
            deleteObjects(bucketName, objectsToDelete);
        }
    }

    private List<String> getListObjectsInFolder(String bucketName, String folderPrefix) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(folderPrefix)
                        .recursive(true)
                        .build()
        );

        List<String> objectsToDelete = new ArrayList<>();
        for (Result<Item> result : results) {
            Item item;
            try {
                item = result.get();
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                logger.log(System.Logger.Level.ERROR,
                        String.format("Error occurred while listing objects in folder '%s' in bucket '%s'", folderPrefix, bucketName),
                        e);
                throw new MinioOperationException();
            }
            objectsToDelete.add(item.objectName());
        }
        return objectsToDelete;
    }

    private void deleteObjects(String bucketName, List<String> objectsToDelete) {
        for (String objectName : objectsToDelete) {
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
                        String.format("Error occurred while deleting object '%s' from bucket '%s'", objectName, bucketName),
                        e);
                throw new MinioOperationException();
            }
        }
    }
}
