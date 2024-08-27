package ru.dragomirov.cloudfilestorage.minio;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.dragomirov.cloudfilestorage.minio.exception.DuplicateItemException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateItemException.class)
    public String handleDuplicateParameterException(DuplicateItemException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error409";
    }
}

