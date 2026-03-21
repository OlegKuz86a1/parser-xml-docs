package com.artwell.api.dto;

import java.util.UUID;

public record UploadResult(
        UUID documentId,
        UUID versionId,
        ValidationResult validation,
        DocumentDetail document
) {}
