package ru.dragomirov.cloudfilestorage.auth.login;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
