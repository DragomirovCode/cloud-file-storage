package ru.dragomirov.cloudfilestorage.minio;

import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.GenericContainer;
import ru.dragomirov.cloudfilestorage.minio.upload.UploadService;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UploadFileTest {

    @Autowired
    UploadService uploadService;

    @Autowired
    GetListObjectService getListObjectService;

    static GenericContainer<?> minioContainer = new GenericContainer<>("minio/minio")
            .withEnv("MINIO_ACCESS_KEY", "minioadmin")
            .withEnv("MINIO_SECRET_KEY", "minioadmin")
            .withCommand("server /data")
            .withExposedPorts(9000);

    static {
        minioContainer.start();
    }

    @DynamicPropertySource
    static void minioProperties(DynamicPropertyRegistry registry) {
        registry.add("minio.url", () -> "http://" +
                minioContainer.getHost() + ":" + minioContainer.getMappedPort(9000));
        registry.add("minio.access.key", () -> "minioadmin");
        registry.add("minio.secret.key", () -> "minioadmin");
    }

    @SneakyThrows
    @Test
    void uploadMultipleFiles_shouldAddFile_inMinio() {
        getListObjectService.listObjects("user-test", "");

        String directory = "C:\\Users\\Твой дом\\OneDrive\\Рабочий стол\\test.png";
        File file = new File(directory);

        try (FileInputStream inputStream = new FileInputStream(file)) {
            MultipartFile multipartFile = new MockMultipartFile(
                    "file",
                    file.getName(),
                    "image/png",
                    inputStream
            );

            MultipartFile[] files = {multipartFile};

            uploadService.uploadMultipleFiles(files, "", "user-test");

            List<String> objectNames = getListObjectService.listObjects("user-test", "").stream()
                    .map(Item::objectName)
                    .toList();

            assertNotNull(objectNames);
            assertEquals(2, objectNames.size());
            assertEquals("test.png", objectNames.get(1));
        }
    }
}
