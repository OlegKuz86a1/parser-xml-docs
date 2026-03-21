package com.artwell.api.dto;

import com.artwell.api.enums.ValidationStatus;

import java.time.Instant;
import java.util.UUID;

public record DocumentVersion(
        UUID id,
        int versionNumber,
        Instant createdAt,
        UUID createdByUserId,
        ValidationStatus validationStatus,
        String fileName,
        long fileSizeBytes
) {}
