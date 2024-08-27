package ru.dragomirov.cloudfilestorage.minio.upload;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UploadService {
    private final MinioClient minioClient;

    @SneakyThrows
    private void uploadFile(String bucketName, String objectName, InputStream fileStream) {
        String folderPath = extractFolderPath(objectName);

        if (!keepFileExists(bucketName, folderPath)) {
            createKeepFile(bucketName, folderPath);
        }

        uploadObject(bucketName, objectName, fileStream);
    }

    private String extractFolderPath(String objectName) {
        return objectName.substring(0, objectName.lastIndexOf('/') + 1);
    }

    private boolean keepFileExists(String bucketName, String folderPath){
        List<Item> objectsInPath = listObjects(bucketName, folderPath);

        for (Item item : objectsInPath) {
            if (item.objectName().equals(folderPath + ".keep")) {
                return true;
            }
        }
        return false;
    }

    private void createKeepFile(String bucketName, String folderPath) throws Exception {
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

    @SneakyThrows
    private void uploadObject(String bucketName, String objectName, InputStream fileStream) {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(fileStream, fileStream.available(), -1)
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

   @SneakyThrows
   @Transactional
   public void uploadMultipleFiles(MultipartFile[] files, String path, String bucketName) {
       for (MultipartFile file : files) {

           InputStream fileStream = file.getInputStream();

           String objectName = path + file.getOriginalFilename();

           uploadFile(bucketName, objectName, fileStream);
       }
   }
}
