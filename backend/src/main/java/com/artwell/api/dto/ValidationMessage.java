package com.artwell.api.dto;

public record ValidationMessage(
        String severity,
        Integer line,
        Integer column,
        String message,
        String xpath
) {}
