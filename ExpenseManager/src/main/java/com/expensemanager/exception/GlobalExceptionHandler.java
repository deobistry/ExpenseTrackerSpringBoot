package com.expensemanager.exception;


import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.expensemanager.dto.response.ErrorResponse;



@RestControllerAdvice
public class GlobalExceptionHandler {



    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String message
    ) {


        ErrorResponse response =
                new ErrorResponse(
                        LocalDateTime.now(),
                        status.value(),
                        message
                );


        return ResponseEntity
                .status(status)
                .body(response);
    }





    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> unauthorized(
            UnauthorizedException ex
    ) {

        return build(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );
    }





    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> forbidden(
            ForbiddenException ex
    ) {

        return build(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );
    }





    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(
            ResourceNotFoundException ex
    ) {

        return build(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }





    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> validation(
            ValidationException ex
    ) {

        return build(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }





    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> generic(
            Exception ex
    ) {

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong"
        );
    }
}	