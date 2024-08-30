package ru.dragomirov.cloudfilestorage.auth;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String messege) {
        super(messege);
    }
}
