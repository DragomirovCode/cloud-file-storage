package ru.dragomirov.cloudfilestorage.minio.home;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dragomirov.cloudfilestorage.minio.delete.DeleteFileService;
import ru.dragomirov.cloudfilestorage.minio.exception.MinioOperationException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final MinioClient minioClient;
    private static final System.Logger logger = System.getLogger(DeleteFileService.class.getName());

    @Transactional(readOnly = true)
    public List<Item> getListObjects(String bucketName, String path) {
        createUserBucket(bucketName);
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
                logger.log(System.Logger.Level.ERROR, e);
                throw new RuntimeException(e);
            }
        }
        return objects;
    }

    public void createUserBucket(String bucketName) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            logger.log(System.Logger.Level.ERROR, e);
            throw new MinioOperationException();
        }
    }

    @Transactional(readOnly = true)
    public List<Item> getObjectByName(String bucketName, String path, String fileName) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(path)
                        .build()
        );

        List<Item> matchingItems = new ArrayList<>();
        for (Result<Item> result : results) {
            try {
                Item item = result.get();
                if (item.objectName().equals(fileName)) {
                    matchingItems.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.log(System.Logger.Level.ERROR, e);
                throw new MinioOperationException();
            }
        }

        return matchingItems;
    }
}
