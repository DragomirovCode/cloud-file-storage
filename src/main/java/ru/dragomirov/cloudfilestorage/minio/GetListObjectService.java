package ru.dragomirov.cloudfilestorage.minio;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dragomirov.cloudfilestorage.minio.create.CreateUserBucketService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetListObjectService {
    private final MinioClient minioClient;
    private final CreateUserBucketService createUserBucketService;

    @Transactional(readOnly = true)
    public List<Item> listObjects(String bucketName, String path) throws Exception {
        createUserBucketService.createUserBucket(bucketName);
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
