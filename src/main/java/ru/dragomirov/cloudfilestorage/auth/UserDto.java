package ru.dragomirov.cloudfilestorage.auth;

import lombok.Data;

@Data
public class UserDto {

    public Long id;
    public String name;
    public String userName;
    public String password;

}
