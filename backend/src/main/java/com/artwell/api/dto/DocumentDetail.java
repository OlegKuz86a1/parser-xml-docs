package com.artwell.api.dto;

import com.artwell.api.enums.DocumentTypeCode;
import com.artwell.api.enums.ValidationStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record DocumentDetail(
        UUID id,
        String documentNumber,
        DocumentTypeCode documentType,
        String title,
        String constructionObjectName,
        UUID constructionObjectId,
        LocalDate documentDate,
        ValidationStatus status,
        int currentVersion,
        Instant updatedAt,
        List<Participant> participants,
        Map<String, Object> extractedMetadata,
        Instant createdAt,
        UUID createdByUserId
) {}
