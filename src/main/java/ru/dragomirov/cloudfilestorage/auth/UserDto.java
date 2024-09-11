package ru.dragomirov.cloudfilestorage.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    public Long id;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[^\\s][\\S]*$", message = "Username cannot start with a space")
    public String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 1, message = "Password must be at least 1 characters long")
    @Pattern(regexp = "^\\S+$", message = "Password cannot contain spaces")
    public String password;

}
