package ru.dragomirov.cloudfilestorage.minio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FileDto {

    public Long id;
    public MultipartFile[] files;

}
