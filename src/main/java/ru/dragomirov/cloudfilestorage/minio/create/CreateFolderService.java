package ru.dragomirov.cloudfilestorage.minio.create;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dragomirov.cloudfilestorage.minio.exception.DuplicateItemException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateFolderService {
    private final MinioClient minioClient;

    @SneakyThrows
    @Transactional
    // FIXME: 30.08.2024 if the path is long, then the folder can still be added with the same naming
    public void createFolder(String bucketName, String folderName, String path) {
        List<String> objectNames = listObjects(bucketName, path).stream()
                .map(Item::objectName)
                .collect(Collectors.toList());

        if (objectNames.contains(path)) {
            throw new DuplicateItemException("A folder with the same name already exists in the specified path");
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

    @SneakyThrows
    private List<Item> listObjects(String bucketName, String path) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(path)
                        .delimiter("/")
                        .build()
        );

        List<Item> objects = new ArrayList<>();
        for (Result<Item> result : results) {
            objects.add(result.get());
        }
        return objects;
    }
}
