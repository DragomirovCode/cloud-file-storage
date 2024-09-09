package ru.dragomirov.cloudfilestorage.minio.get;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.dragomirov.cloudfilestorage.minio.exception.MinioOperationException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetFileByNameService {
    private final MinioClient minioClient;

    public List<Item> getObjectByName(String bucketName, String path, String fileName) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(path)
                        .build()
        );

        for (Result<Item> result : results) {
            try {
                Item item = result.get();
                if (item.objectName().equals(fileName)) {
                    return Collections.singletonList(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new MinioOperationException();
            }
        }
        return Collections.emptyList();
    }
}
