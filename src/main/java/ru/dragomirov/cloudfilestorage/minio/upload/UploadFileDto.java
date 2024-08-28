package ru.dragomirov.cloudfilestorage.minio.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class UploadFileDto {

    public MultipartFile[] files;

}
