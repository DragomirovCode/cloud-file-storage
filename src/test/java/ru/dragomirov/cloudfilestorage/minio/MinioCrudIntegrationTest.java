package ru.dragomirov.cloudfilestorage.minio;

import io.minio.messages.Item;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.dragomirov.cloudfilestorage.minio.create.CreateFolderService;
import ru.dragomirov.cloudfilestorage.minio.delete.DeleteFileService;
import ru.dragomirov.cloudfilestorage.minio.delete.DeleteFolderService;
import ru.dragomirov.cloudfilestorage.minio.home.HomeService;
import ru.dragomirov.cloudfilestorage.minio.update.UpdateFileService;
import ru.dragomirov.cloudfilestorage.minio.update.UpdateFolderService;
import ru.dragomirov.cloudfilestorage.minio.upload.UploadService;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MinioCrudIntegrationTest {

    @Autowired
    UploadService uploadService;

    @Autowired
    HomeService homeService;

    @Autowired
    UpdateFileService updateFileService;

    @Autowired
    CreateFolderService createFolderService;

    @Autowired
    UpdateFolderService updateFolderService;

    @Autowired
    DeleteFileService deleteFileService;

    @Autowired
    DeleteFolderService deleteFolderService;

    @Container
    static final GenericContainer<?> minioContainer = new GenericContainer<>("minio/minio")
            .withCommand("server /data")
            .withEnv("MINIO_ROOT_USER", "minioadmin")
            .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
            .withExposedPorts(9000);

    @Container
    static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("database")
            .withUsername("my_mysql_username")
            .withPassword("my_mysql_password");

    static {
        mySQLContainer.start();
        minioContainer.start();
    }

    @DynamicPropertySource
    static void minioProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("minio.url", () -> "http://" +
                minioContainer.getHost() + ":" + minioContainer.getMappedPort(9000));
        registry.add("minio.access.key", () -> "minioadmin");
        registry.add("minio.secret.key", () -> "minioadmin");
    }

    @SneakyThrows
    @Test
    @Order(1)
    @DisplayName("process upload should add file in minio")
    void uploadMultipleFiles_shouldAddFile_inMinio() {
        homeService.createUserBucket("user-test");

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

            List<String> objectNames = homeService.getListObjects("user-test", "").stream()
                    .map(Item::objectName)
                    .toList();

            assertNotNull(objectNames);
            assertEquals(2, objectNames.size());
            assertEquals("test.png", objectNames.get(1));
        }
    }

    @Test
    @Order(2)
    @DisplayName("process update should update file name in minio")
    void updateFile_shouldUpdateFileName_inMinio() {
        updateFileService.updateFile("user-test", "test.png", "new-test.png", "");

        List<String> objectNames = homeService.getListObjects("user-test", "").stream()
                .map(Item::objectName)
                .toList();

        assertNotNull(objectNames);
        assertEquals(2, objectNames.size());
        assertEquals("new-test.png", objectNames.get(1));
    }

    @Test
    @Order(3)
    @DisplayName("process update should update folder name in minio")
    void updateFolder_shouldUpdateFolderName_inMinio() {
        createFolderService.createFolder("user-test", "folder", "");

        updateFolderService.updateNameFolder("user-test", "folder", "new-folder", "");

        List<String> objectNames = homeService.getListObjects("user-test", "").stream()
                .map(Item::objectName)
                .toList();

        assertNotNull(objectNames);
        assertEquals(3, objectNames.size());
        assertEquals("new-folder/", objectNames.get(2));
    }

    @Test
    @Order(4)
    @DisplayName("process delete should delete file in minio")
    void deleteFile_shouldDeleteFile_inMinio() {
        deleteFileService.deleteFile("user-test", "new-test.png");

        List<String> objectNames = homeService.getListObjects("user-test", "").stream()
                .map(Item::objectName)
                .toList();

        assertNotNull(objectNames);
        assertEquals(2, objectNames.size());
        assertEquals(".keep", objectNames.get(0));
        assertEquals("new-folder/", objectNames.get(1));
    }

    @Test
    @Order(5)
    @DisplayName("process delete should delete folder in minio")
    void deleteFolder_shouldDeleteFolder_inMinio() {
        deleteFolderService.deleteFolder("user-test", "new-folder/");

        List<String> objectNames = homeService.getListObjects("user-test", "").stream()
                .map(Item::objectName)
                .toList();

        assertNotNull(objectNames);
        assertEquals(1, objectNames.size());
        assertEquals(".keep", objectNames.get(0));
    }
}
