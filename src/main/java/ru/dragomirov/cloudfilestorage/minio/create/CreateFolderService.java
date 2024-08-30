package ru.dragomirov.cloudfilestorage.minio.create;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dragomirov.cloudfilestorage.minio.exception.DuplicateItemException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class CreateFolderService {
    private final MinioClient minioClient;

    @SneakyThrows
    @Transactional
    public void createFolder(String bucketName, String folderName, String path) {
        folderName = folderName.endsWith("/") ? folderName : folderName + "/";

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
    }
}
