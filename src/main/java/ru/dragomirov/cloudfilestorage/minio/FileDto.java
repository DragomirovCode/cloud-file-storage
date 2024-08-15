package ru.dragomirov.cloudfilestorage.minio;

import lombok.Data;

@Data
public class FileDto {

    public Long id;
    public String name;
    public String minioPath;
    public FolderDto folderDto;

}
