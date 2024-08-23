package ru.dragomirov.cloudfilestorage.minio;

import io.minio.*;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
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
    public void editFolder(String bucketName, String oldFolderName, String newFolderName, String pathFile) {
        List<Item> items = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(oldFolderName)
                        .recursive(true)
                        .build()
        );

        for (Result<Item> result : results) {
            items.add(result.get());
        }

        for (Item item : items) {
            String oldObjectName = item.objectName();
            if (oldObjectName.startsWith(oldFolderName)) {
                String relativePath = oldObjectName.substring(oldFolderName.length());
                String newObjectName = pathFile + "/" + newFolderName + "/" + relativePath;

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(bucketName)
                                .object(newObjectName)
                                .source(CopySource.builder().bucket(bucketName).object(oldObjectName).build())
                                .build()
                );
            }
        }

        for (Item item : items) {
            String oldObjectName = item.objectName();
            if (oldObjectName.startsWith(oldFolderName)) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder().bucket(bucketName).object(oldObjectName).build()
                );
            }
        }
    }


    @SneakyThrows
    public void editFile(String bucketName, String oldObjectName, String newObjectName) {

        CopySource copySource = CopySource.builder()
                .bucket(bucketName)
                .object(oldObjectName)
                .build();

        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(bucketName)
                        .object(newObjectName)
                        .source(copySource)
                        .build()
        );

        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(oldObjectName)
                        .build()
        );

    }

    @SneakyThrows
    public void downloadFile(String bucketName, String objectName, String destinationFilePath) {

        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());

        FileOutputStream fos = new FileOutputStream(destinationFilePath);
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
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
