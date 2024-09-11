package ru.dragomirov.cloudfilestorage.minio.update;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateFolderDto {

    @NotNull(message = "The file name should not be null")
    @NotEmpty(message = "The file name should not be empty")
    @NotBlank(message = "The file name cannot be blank")
    @Size(min = 3, max = 63, message = "The file name must be between 3 and 63 characters")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9-_ ]*$", message = "The folder name must start with a letter and contain only English letters, digits, hyphens, underscores, and spaces")
    public String folder;

}
