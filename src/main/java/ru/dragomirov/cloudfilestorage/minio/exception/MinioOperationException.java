package ru.dragomirov.cloudfilestorage.minio.exception;

public class MinioOperationException extends RuntimeException {
    public MinioOperationException(String message) {
        super(message);
    }
}
