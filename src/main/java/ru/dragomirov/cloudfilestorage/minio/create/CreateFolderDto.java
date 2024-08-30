package ru.dragomirov.cloudfilestorage.minio.create;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateFolderDto {

    @NotNull(message = "The folder name should not be null")
    @NotEmpty(message = "The folder name should not be empty")
    @Size(min = 3, max = 63, message = "The folder name must be between 3 and 63 characters")
    @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "The folder name must contain only English letters, digits, and hyphens")
    public String folder;

}
