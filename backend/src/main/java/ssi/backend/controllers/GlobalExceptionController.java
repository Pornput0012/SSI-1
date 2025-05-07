package ssi.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ssi.backend.dtos.ErrorResponseDTO;
import ssi.backend.exceptions.ItemNotFoundException;

@RestControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleException(
            ItemNotFoundException exception, WebRequest request) {

        return new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                request.getDescription(false)
        );
    }
}