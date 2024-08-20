package ru.dragomirov.cloudfilestorage.minio;

import io.minio.*;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinioService {
    private final MinioClient minioClient;

    @Autowired
    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @SneakyThrows
    public void createFolder(String bucketName, String folderName) {
        InputStream keepFileStream = new ByteArrayInputStream(new byte[0]);
        String objectName = folderName + "/.keep";

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(keepFileStream, 0, -1)
                        .build()
        );
    }


    public void uploadFile(String bucketName, String objectName, InputStream fileStream) throws Exception {
        String folderPath = objectName.substring(0, objectName.lastIndexOf('/') + 1);

        // Проверяем, существует ли скрытый файл в этой "папке"
        List<Item> objectsInPath = listObjects(bucketName, folderPath);
        boolean keepFileExists = false;

        // Проходим по всем объектам и проверяем наличие скрытого файла
        for (Item item : objectsInPath) {
            if (item.objectName().equals(folderPath + ".keep")) {
                keepFileExists = true;
                break; // Прерываем цикл, если файл найден
            }
        }

        // Если скрытый файл отсутствует, загружаем его
        if (!keepFileExists) {
            InputStream keepFileStream = new ByteArrayInputStream(new byte[0]);
            String keepFileName = folderPath + ".keep";
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(keepFileName)
                            .stream(keepFileStream, 0, -1)
                            .build()
            );
        }

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(fileStream, fileStream.available(), -1)
                        .build()
        );
    }

    public List<Item> listObjects(String bucketName, String path) throws Exception {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(path)  // Указываем путь для фильтрации
                        .delimiter("/")  // Используем, чтобы разделять папки
                        .build()
        );

        List<Item> objects = new ArrayList<>();
        for (Result<Item> result : results) {
            objects.add(result.get());
        }
        return objects;
    }

    public void deleteFile(String bucketName, String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    @SneakyThrows
    public void deleteFolder(String bucketName, String folderPrefix) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(folderPrefix)
                        .recursive(true)
                        .build()
        );

        List<String> objectsToDelete = new ArrayList<>();
        for (Result<Item> result : results) {
            Item item = result.get();
            objectsToDelete.add(item.objectName());
        }

        if (!objectsToDelete.isEmpty()) {
            for (String objectName : objectsToDelete) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                );

            }
        }
    }
}
