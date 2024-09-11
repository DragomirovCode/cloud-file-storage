package ru.dragomirov.cloudfilestorage.minio.create;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateFolderDto {

    @NotNull(message = "The folder name should not be null")
    @NotEmpty(message = "The folder name should not be empty")
    @NotBlank(message = "The folder name cannot be blank")
    @Size(min = 3, max = 63, message = "The folder name must be between 3 and 63 characters")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9-_ ]*$", message = "The folder name must start with a letter and contain only English letters, digits, hyphens, underscores, and spaces")
    public String folder;

}
