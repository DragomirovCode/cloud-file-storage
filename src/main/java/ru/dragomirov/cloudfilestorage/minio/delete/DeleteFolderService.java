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
    private final MinioClient minioClient;

    @Transactional
    public void deleteFolder(String bucketName, String folderPrefix) {
        List<String> objectsToDelete = listObjectsInFolder(bucketName, folderPrefix);
        if (!objectsToDelete.isEmpty()) {
            deleteObjects(bucketName, objectsToDelete);
        }
    }

    private List<String> listObjectsInFolder(String bucketName, String folderPrefix) {
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
                throw new MinioOperationException();
            }
        }
    }
}
