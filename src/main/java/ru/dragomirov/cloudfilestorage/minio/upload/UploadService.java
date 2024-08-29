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
import ru.dragomirov.cloudfilestorage.minio.FileUtil;
import ru.dragomirov.cloudfilestorage.minio.PathUtil;
import ru.dragomirov.cloudfilestorage.minio.exception.DuplicateItemException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UploadService {
    private final MinioClient minioClient;
    private final FileUtil fileUtil;
    private final PathUtil pathUtil;

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
   // FIXME: 29.08.2024 you need to replace the empty path with the path "home" | the logic of adding files does not work
   public void uploadMultipleFiles(MultipartFile[] files, String path, String bucketName) {
       List<String> objectNames = listObjects(bucketName, path).stream()
               .map(Item::objectName)
               .collect(Collectors.toList());

       for (MultipartFile file : files) {

           InputStream fileStream = file.getInputStream();

           String objectName = path + file.getOriginalFilename();

           String pathFolder = pathUtil.getParentPathSafe(objectName);
           pathFolder = pathFolder + "/";
           String folderName = fileUtil.folderName(pathFolder);


           if (objectNames.contains(folderName + "/")) {
               throw new DuplicateItemException("A folder with the same name already exists in the specified path");
           }

           if (objectNames.contains(objectName)) {
               throw new DuplicateItemException("A file with the same name already exists in the specified path");
           }

           uploadFile(bucketName, objectName, fileStream);
       }
   }
}
