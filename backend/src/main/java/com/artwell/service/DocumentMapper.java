package com.artwell.service;

import com.artwell.api.dto.DocumentDetail;
import com.artwell.api.dto.DocumentSummary;
import com.artwell.api.dto.DocumentVersion;
import com.artwell.api.dto.Participant;
import com.artwell.domain.entity.ConstructionDocument;
import com.artwell.domain.entity.DocumentVersionEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class DocumentMapper {

    public DocumentSummary toSummary(ConstructionDocument d) {
        return new DocumentSummary(
                d.getId(),
                d.getDocumentNumber(),
                d.getDocumentType(),
                d.getTitle(),
                d.getConstructionObjectName(),
                d.getConstructionObjectId(),
                d.getDocumentDate(),
                d.getStatus(),
                d.getCurrentVersionNumber(),
                d.getUpdatedAt()
        );
    }

    @SuppressWarnings("unchecked")
    public DocumentDetail toDetail(ConstructionDocument d) {
        Map<String, Object> meta = d.getExtractedMetadata() != null ? d.getExtractedMetadata() : Map.of();
        List<Participant> participants = Collections.emptyList();
        if (meta.get("participants") instanceof List<?> list) {
            participants = list.stream()
                    .filter(o -> o instanceof Map)
                    .map(o -> {
                        Map<String, Object> m = (Map<String, Object>) o;
                        return new Participant(
                                String.valueOf(m.getOrDefault("name", "")),
                                String.valueOf(m.getOrDefault("role", "")),
                                String.valueOf(m.getOrDefault("organization", ""))
                        );
                    })
                    .toList();
        }
        return new DocumentDetail(
                d.getId(),
                d.getDocumentNumber(),
                d.getDocumentType(),
                d.getTitle(),
                d.getConstructionObjectName(),
                d.getConstructionObjectId(),
                d.getDocumentDate(),
                d.getStatus(),
                d.getCurrentVersionNumber(),
                d.getUpdatedAt(),
                participants,
                meta,
                d.getCreatedAt(),
                d.getCreatedByUserId()
        );
    }

    public DocumentVersion toVersionDto(DocumentVersionEntity v) {
        return new DocumentVersion(
                v.getId(),
                v.getVersionNumber(),
                v.getCreatedAt(),
                v.getCreatedByUserId(),
                v.getValidationStatus(),
                v.getFileName(),
                v.getFileSizeBytes()
        );
    }
}
