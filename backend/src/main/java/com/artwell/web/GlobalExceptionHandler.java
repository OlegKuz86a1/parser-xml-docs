package com.artwell.web;

import com.artwell.api.dto.ErrorResponse;
import com.artwell.api.dto.ValidationResult;
import com.artwell.web.exception.BadRequestException;
import com.artwell.web.exception.NotFoundException;
import com.artwell.web.exception.UnauthorizedException;
import com.artwell.web.exception.ValidationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", e.getMessage(), List.of()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequest(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("BAD_REQUEST", e.getMessage(), List.of()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> unauthorized(UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("UNAUTHORIZED", e.getMessage(), List.of()));
    }

    @ExceptionHandler(ValidationFailedException.class)
    public ResponseEntity<ErrorResponse> validationFailed(ValidationFailedException e) {
        ValidationResult vr = e.getValidationResult();
        String msg = vr.messages().isEmpty() ? "Validation failed" : vr.messages().get(0).message();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_FAILED", msg, vr.messages().stream().map(m -> m.message()).toList()));
    }
}
