package com.artwell.api.dto;

import com.artwell.api.enums.AuditEventType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AuditEntry(
        UUID id,
        Instant occurredAt,
        AuditEventType eventType,
        UUID userId,
        String userDisplayName,
        UUID documentId,
        String documentNumber,
        String details,
        Map<String, Object> payloadJson
) {}
