package ru.dragomirov.cloudfilestorage.minio.update;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dragomirov.cloudfilestorage.minio.exception.DuplicateItemException;
import ru.dragomirov.cloudfilestorage.minio.exception.MinioOperationException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateFileService {
    private static final System.Logger logger = System.getLogger(UpdateFileService.class.getName());
    private final MinioClient minioClient;

    @Transactional
    public void updateFile(String bucketName, String oldObjectName, String newObjectName, String path) {
        List<String> objectNames;
        try {
            objectNames = getListObjects(bucketName, path).stream()
                    .map(Item::objectName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(System.Logger.Level.ERROR,
                    String.format("Error occurred while listing objects in path '%s' in bucket '%s'", path, bucketName),
                    e);
            throw new MinioOperationException();
        }

        if (objectNames.contains(newObjectName)) {
            throw new DuplicateItemException("A file with the same name already exists in the specified path");
        }

        try {
            copyObject(bucketName, oldObjectName, newObjectName);
            removeObject(bucketName, oldObjectName);
        } catch (Exception e) {
            logger.log(System.Logger.Level.ERROR,
                    String.format("Error occurred while updating file '%s' to '%s' in bucket '%s'", oldObjectName, newObjectName, bucketName),
                    e);
            throw new MinioOperationException();
        }
    }

    private void copyObject(String bucketName, String sourceObjectName, String destinationObjectName) {
        CopySource copySource = CopySource.builder()
                .bucket(bucketName)
                .object(sourceObjectName)
                .build();

        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(destinationObjectName)
                            .source(copySource)
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            logger.log(System.Logger.Level.ERROR,
                    String.format("Error occurred while copying object '%s' to '%s' in bucket '%s'", sourceObjectName, destinationObjectName, bucketName),
                    e);
            throw new MinioOperationException();
        }
    }

    private void removeObject(String bucketName, String objectName) {
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
                    String.format("Error occurred while removing object '%s' from bucket '%s'", objectName, bucketName),
                    e);
            throw new MinioOperationException();
        }
    }

    private List<Item> getListObjects(String bucketName, String path) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(path)
                        .delimiter("/")
                        .build()
        );

        List<Item> objects = new ArrayList<>();
        for (Result<Item> result : results) {
            try {
                objects.add(result.get());
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                logger.log(System.Logger.Level.ERROR,
                        String.format("Error occurred while listing objects in path '%s' in bucket '%s'", path, bucketName),
                        e);
                throw new MinioOperationException();
            }
        }
        return objects;
    }
}
