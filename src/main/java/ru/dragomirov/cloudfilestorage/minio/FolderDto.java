package ru.dragomirov.cloudfilestorage.minio;

import lombok.Data;

import java.util.List;

@Data
public class FolderDto {

    public Long id;
    public String name;
    public List<FileDto> files;

}
