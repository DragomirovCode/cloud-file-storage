package ru.dragomirov.cloudfilestorage.minio.upload;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.dragomirov.cloudfilestorage.minio.FileUtil;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;
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
public class UploadService {
    private static final System.Logger logger = System.getLogger(UploadService.class.getName());
    private final MinioClient minioClient;
    private final FileUtil fileUtil;
    private final PathUtil pathUtil;

    private void uploadFile(String bucketName, String objectName, InputStream fileStream) {
        String folderPath = extractFolderPath(objectName);

        if (!keepFileExists(bucketName, folderPath)) {
            try {
                createKeepFile(bucketName, folderPath);
            } catch (Exception e) {
                logger.log(System.Logger.Level.ERROR,
                        String.format("Failed to create .keep file for folder '%s' in bucket '%s'", folderPath, bucketName),
                        e);
                throw new MinioOperationException();
            }
        }

        uploadObject(bucketName, objectName, fileStream);
    }

    private String extractFolderPath(String objectName) {
        return objectName.substring(0, objectName.lastIndexOf('/') + 1);
    }

    private boolean keepFileExists(String bucketName, String folderPath) {
        List<Item> objectsInPath = getListObjects(bucketName, folderPath);

        for (Item item : objectsInPath) {
            if (item.objectName().equals(folderPath + ".keep")) {
                return true;
            }
        }
        return false;
    }

    private void createKeepFile(String bucketName, String folderPath) {
        InputStream keepFileStream = new ByteArrayInputStream(new byte[0]);
        String keepFileName = folderPath + ".keep";
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(keepFileName)
                            .stream(keepFileStream, 0, -1)
                            .build()
            );
        } catch (Exception e) {
            logger.log(System.Logger.Level.ERROR,
                    String.format("Failed to create .keep file '%s' in bucket '%s'", keepFileName, bucketName),
                    e);
            throw new MinioOperationException();
        }
    }

    private void uploadObject(String bucketName, String objectName, InputStream fileStream) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(fileStream, fileStream.available(), -1)
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            logger.log(System.Logger.Level.ERROR,
                    String.format("Failed to upload object '%s' to bucket '%s'", objectName, bucketName),
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
                        String.format("Failed to list objects in path '%s' in bucket '%s'", path, bucketName),
                        e);
                throw new MinioOperationException();
            }
        }
        return objects;
    }

    @Transactional
    public void uploadMultipleFiles(MultipartFile[] files, String path, String bucketName) {
        try {

            List<String> objectNames = getListObjects(bucketName, path).stream()
                    .map(Item::objectName)
                    .collect(Collectors.toList());

            for (MultipartFile file : files) {

                InputStream fileStream;

                fileStream = file.getInputStream();

                String objectName = path + file.getOriginalFilename();

                String pathFolder = pathUtil.getParentPathSafe(objectName);

                pathFolder = pathFolder.endsWith("/") ? pathFolder : pathFolder + "/";

                String folderName = fileUtil.folderName(pathFolder);

                if (objectNames.contains(folderName + "/")) {
                    throw new DuplicateItemException("A folder with the same name already exists in the specified path");
                }

                if (objectNames.contains(objectName)) {
                    throw new DuplicateItemException("A file with the same name already exists in the specified path");
                }

                uploadFile(bucketName, objectName, fileStream);

            }
        } catch (IOException e) {
            logger.log(System.Logger.Level.ERROR,
                    String.format("Failed to process multiple files for path '%s' in bucket '%s'", path, bucketName),
                    e);
            throw new MinioOperationException();
        }
    }
}
