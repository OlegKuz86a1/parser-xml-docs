package com.artwell.web.exception;

import com.artwell.api.dto.ValidationResult;
import lombok.Getter;

@Getter
public class ValidationFailedException extends RuntimeException {

    private final ValidationResult validationResult;

    public ValidationFailedException(ValidationResult validationResult) {
        super("XSD validation failed");
        this.validationResult = validationResult;
    }
}
