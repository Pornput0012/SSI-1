package ssi.backend.exceptions;

import ssi.backend.dtos.ErrorResponseDTO;

public class BadRequestException extends RuntimeException{
    private final ErrorResponseDTO errorResponse;


    public BadRequestException(String message) {
        super(message);
        this.errorResponse = null;
    }

    public BadRequestException(ErrorResponseDTO errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }

    public ErrorResponseDTO getErrorResponse() {
        return errorResponse;
    }
}
