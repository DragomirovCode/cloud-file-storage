package ru.dragomirov.cloudfilestorage.commons;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import ru.dragomirov.cloudfilestorage.CloudFileStorageApplication;

public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CloudFileStorageApplication.class);
    }
}
