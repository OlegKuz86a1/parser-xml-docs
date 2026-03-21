package com.artwell.api.dto;

import com.artwell.api.enums.DocumentTypeCode;
import com.artwell.api.enums.UserRole;

import java.util.List;
import java.util.UUID;

public record CurrentUser(
        UUID userId,
        String displayName,
        UserRole role,
        List<DocumentTypeCode> allowedDocumentTypes,
        List<UUID> constructionObjectIds
) {}
