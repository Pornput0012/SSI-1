package ssi.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ssi.backend.dtos.ErrorResponseDTO;

@RestControllerAdvice
public class GlobalExceptionController {
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponseDTO handleException(
//            Exception exception, WebRequest request) {
//
//        return new ErrorResponseDTO(
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "Validation error. Check 'errors' field for details.",
//                request.getDescription(false)
//        );
//    }
}
