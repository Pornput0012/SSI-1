package ssi.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import ssi.backend.dtos.ErrorResponseDTO;
import ssi.backend.exceptions.*;

@RestControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleException(
            ItemNotFoundException exception, WebRequest request) {

        return new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleException(MethodArgumentNotValidException exception, WebRequest request) {

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                request.getDescription(false)
        );

        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        });

        return errorResponse;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDTO handleException(RuntimeException exception, WebRequest request) {
        // อย่าจับ AccessDeniedException
        if (exception instanceof AccessDeniedException) {
            throw exception; // ให้ Spring Security handle → 403
        }
        return new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exception.getMessage(),
                request.getDescription(false));
    }


    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleException(
            BadRequestException exception, WebRequest request) {

        // ถ้ามี ErrorResponseDTO ให้ส่งคืนทั้งก้อนเลย
        if (exception.getErrorResponse() != null) {
            return exception.getErrorResponse();
        }

        // ถ้าไม่มี (กรณี throw แค่ข้อความ)
        return new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false)
        );
    }


    @ExceptionHandler(ConfilctResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleException(
            ConfilctResourceException exception, WebRequest request) {

        return exception.getMessage();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO handleException(
            UnauthorizedException exception, WebRequest request) {

        return new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDTO handleException(
            ForbiddenException exception, WebRequest request) {

        return new ErrorResponseDTO(
                HttpStatus.FORBIDDEN.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleException(
            IllegalArgumentException exception, WebRequest request) {

        return new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleException(
            NoHandlerFoundException exception, WebRequest request) {

        return new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "Endpoint not found: " + exception.getRequestURL(),
                request.getDescription(false)
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDTO handleException(Exception exception, WebRequest request) {

        return new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error : " + exception.getClass().getSimpleName(),
                request.getDescription(false));
    }



}