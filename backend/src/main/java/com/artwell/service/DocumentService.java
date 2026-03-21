package com.artwell.service;

import com.artwell.api.dto.AuditEntry;
import com.artwell.api.dto.DocumentDetail;
import com.artwell.api.dto.DocumentVersion;
import com.artwell.api.dto.PageDocumentSummary;
import com.artwell.api.dto.UploadResult;
import com.artwell.api.dto.ValidationResult;
import com.artwell.api.enums.DocumentTypeCode;
import com.artwell.api.enums.VersionUploadMode;
import com.artwell.api.enums.ValidationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface DocumentService {

    PageDocumentSummary listDocuments(
            ValidationStatus status,
            DocumentTypeCode documentType,
            UUID constructionObjectId,
            String documentNumber,
            Pageable pageable
    );

    DocumentDetail getDocument(UUID id);

    byte[] getCurrentXml(UUID documentId);

    UploadResult upload(
            MultipartFile file,
            String documentNumberQuery,
            VersionUploadMode versionMode,
            UUID constructionObjectId,
            UUID actingUserId
    );

    List<DocumentVersion> listVersions(UUID documentId);

    byte[] getVersionXml(UUID documentId, UUID versionId);

    List<AuditEntry> listEvents(UUID documentId);

    ValidationResult validateOnly(MultipartFile file);
}
