package ru.dragomirov.cloudfilestorage.minio;

import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.dragomirov.cloudfilestorage.auth.User;
import ru.dragomirov.cloudfilestorage.auth.UserService;
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
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

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

    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("cloud-file-storage")
            .withUsername("postgres")
            .withPassword("postgres");

    static {
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
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
        User newUser = new User("test", passwordEncoder.encode("123"));
        userService.save(newUser);

        getListObjectService.listObjects("user-test", "");

        String directory = "C:\\Users\\Твой дом\\OneDrive\\Рабочий стол\\test.png";
        File file = new File(directory);

        FileInputStream inputStream = new FileInputStream(file);
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
