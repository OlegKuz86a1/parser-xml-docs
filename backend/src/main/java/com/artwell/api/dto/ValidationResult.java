package com.artwell.api.dto;

import com.artwell.api.enums.ValidationStatus;

import java.util.List;

public record ValidationResult(
        ValidationStatus status,
        List<ValidationMessage> messages
) {}
