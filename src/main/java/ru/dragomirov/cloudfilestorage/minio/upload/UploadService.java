package ru.dragomirov.cloudfilestorage.minio.upload;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class UploadService {
    private final MinioClient minioClient;

    @Autowired
    public UploadService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public void uploadFile(String bucketName, String objectName, InputStream fileStream) throws Exception {
        String folderPath = objectName.substring(0, objectName.lastIndexOf('/') + 1);

        List<Item> objectsInPath = listObjects(bucketName, folderPath);
        boolean keepFileExists = false;

        for (Item item : objectsInPath) {
            if (item.objectName().equals(folderPath + ".keep")) {
                keepFileExists = true;
                break;
            }
        }

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
