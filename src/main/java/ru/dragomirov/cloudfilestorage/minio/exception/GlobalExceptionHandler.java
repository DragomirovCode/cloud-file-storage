package ru.dragomirov.cloudfilestorage.minio.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.dragomirov.cloudfilestorage.auth.DuplicateUserException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateItemException.class)
    public String handleDuplicateParameterException(DuplicateItemException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error500";
    }

    @ExceptionHandler(InvalidParameterException.class)
    public String handleInvalidParameterException(InvalidParameterException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error500";
    }

    @ExceptionHandler(MinioOperationException.class)
    public String handleMinioOperationParameterException(MinioOperationException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error410";
    }

    @ExceptionHandler(DuplicateUserException.class)
    public String handleDuplicateUserParameterException() {
        return "error409";
    }
}
