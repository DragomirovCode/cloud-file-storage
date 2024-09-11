package ru.dragomirov.cloudfilestorage.minio.create;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dragomirov.cloudfilestorage.minio.exception.DuplicateItemException;
import ru.dragomirov.cloudfilestorage.minio.exception.MinioOperationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateFolderService {
    private static final System.Logger logger = System.getLogger(CreateFolderService.class.getName());
    private final MinioClient minioClient;

    @Transactional
    public void createFolder(String bucketName, String folderName, String path) {
        try {
            List<String> objectNames = getListObjects(bucketName, path).stream()
                    .map(Item::objectName)
                    .collect(Collectors.toList());

            folderName = folderName.endsWith("/") ? folderName : folderName + "/";

            if (objectNames.contains(folderName)) {
                throw new DuplicateItemException("A folder with the same name already exists in the specified path");
            }

            if (path.endsWith(folderName)) {
                throw new DuplicateItemException("The path cannot contain two consecutive identical folder names");
            }

            InputStream keepFileStream = new ByteArrayInputStream(new byte[0]);
            String objectName = path + folderName + "/.keep";

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(keepFileStream, 0, -1)
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException |
                 IOException e) {
            logger.log(System.Logger.Level.ERROR, "Error occurred while creating folder in Minio", e);
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
                logger.log(System.Logger.Level.ERROR, "Error occurred while listing objects in Minio", e);
                throw new MinioOperationException();
            }
        }
        return objects;
    }
}
