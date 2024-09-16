package ru.dragomirov.cloudfilestorage.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.dragomirov.cloudfilestorage.auth.registration.DuplicateUserException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
public class UserRegistrationTest {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Container
    static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("database")
            .withUsername("my_mysql_username")
            .withPassword("my_mysql_password");

    @Container
    static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.4.0")
            .withExposedPorts(6379);

    static {
        mySQLContainer.start();
        redisContainer.start();
    }

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Test
    @Order(1)
    @DisplayName("process save should add user in database")
    void save_shouldAddUser_inDatabase() {
        User newUser = new User("user", passwordEncoder.encode("123"));
        userService.save(newUser);

        List<User> users = userService.getAll();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("user", users.get(0).getUsername());
    }

    @Test
    @Order(2)
    @DisplayName("process save should throw exception not unique username")
    void save_shouldThrowException_notUniqueUsername() {
        User newUser = new User("user", passwordEncoder.encode("123"));

        assertThrows(DuplicateUserException.class, () ->
                userService.save(newUser));
    }
}
