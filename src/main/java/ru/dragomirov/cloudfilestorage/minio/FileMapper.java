package ru.dragomirov.cloudfilestorage.minio;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper {
    FileDto toDto(File file);
    File toEntity(FileDto fileDto);
}
