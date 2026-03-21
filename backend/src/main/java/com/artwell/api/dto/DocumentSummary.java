package com.artwell.api.dto;

import com.artwell.api.enums.DocumentTypeCode;
import com.artwell.api.enums.ValidationStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record DocumentSummary(
        UUID id,
        String documentNumber,
        DocumentTypeCode documentType,
        String title,
        String constructionObjectName,
        UUID constructionObjectId,
        LocalDate documentDate,
        ValidationStatus status,
        int currentVersion,
        Instant updatedAt
) {}
