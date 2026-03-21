package com.artwell.service;

import com.artwell.api.dto.DocumentDetail;
import com.artwell.api.dto.DocumentVersion;
import com.artwell.api.dto.PageDocumentSummary;
import com.artwell.api.dto.UploadResult;
import com.artwell.api.dto.ValidationResult;
import com.artwell.api.enums.AuditEventType;
import com.artwell.api.enums.DocumentTypeCode;
import com.artwell.api.enums.ValidationStatus;
import com.artwell.api.enums.VersionUploadMode;
import com.artwell.domain.entity.ConstructionDocument;
import com.artwell.domain.entity.DocumentEventEntity;
import com.artwell.domain.entity.DocumentVersionEntity;
import com.artwell.domain.repository.ConstructionDocumentRepository;
import com.artwell.domain.repository.DocumentEventRepository;
import com.artwell.domain.repository.DocumentVersionRepository;
import com.artwell.web.exception.BadRequestException;
import com.artwell.web.exception.NotFoundException;
import com.artwell.web.exception.ValidationFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final ConstructionDocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;
    private final DocumentEventRepository eventRepository;
    private final XmlWellformedChecker xmlWellformedChecker;
    private final XsdValidationService xsdValidationService;
    private final DocumentMetadataExtractor metadataExtractor;
    private final DocumentMapper documentMapper;

    @Override
    @Transactional(readOnly = true)
    public PageDocumentSummary listDocuments(
            ValidationStatus status,
            DocumentTypeCode documentType,
            UUID constructionObjectId,
            String documentNumber,
            Pageable pageable
    ) {
        Page<ConstructionDocument> page = documentRepository.search(
                status,
                documentType,
                constructionObjectId,
                documentNumber,
                pageable
        );
        return new PageDocumentSummary(
                page.getContent().stream().map(documentMapper::toSummary).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentDetail getDocument(UUID id) {
        ConstructionDocument d = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("document", id));
        return documentMapper.toDetail(d);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getCurrentXml(UUID documentId) {
        ConstructionDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("document", documentId));
        List<DocumentVersionEntity> versions = versionRepository.findByDocumentOrderByVersionNumberDesc(doc);
        return versions.stream()
                .max(Comparator.comparingInt(DocumentVersionEntity::getVersionNumber))
                .map(DocumentVersionEntity::getXmlContent)
                .orElseThrow(() -> new NotFoundException("Нет сохранённой версии XML для документа"));
    }

    @Override
    @Transactional
    public UploadResult upload(
            MultipartFile file,
            String documentNumberQuery,
            VersionUploadMode versionMode,
            UUID constructionObjectId,
            UUID actingUserId
    ) {
        byte[] xmlBytes;
        try {
            xmlBytes = file.getBytes();
        } catch (IOException e) {
            throw new BadRequestException("Не удалось прочитать файл: " + e.getMessage());
        }

        try {
            xmlWellformedChecker.assertWellFormed(xmlBytes);
        } catch (Exception e) {
            throw new BadRequestException("XML не well-formed: " + e.getMessage());
        }

        Optional<String> explicitNum = Optional.ofNullable(documentNumberQuery).filter(s -> !s.isBlank());
        DocumentMetadataExtractor.Extracted extracted = metadataExtractor.extract(xmlBytes, explicitNum);

        ValidationResult xsdResult = xsdValidationService.validate(xmlBytes, extracted.documentType());
        if (xsdResult.status() == ValidationStatus.INVALID) {
            throw new ValidationFailedException(xsdResult);
        }

        ValidationStatus docStatus = xsdResult.messages().stream().anyMatch(m -> "WARNING".equals(m.severity()))
                ? ValidationStatus.PENDING
                : ValidationStatus.VALID;

        String number = extracted.documentNumber();
        ConstructionDocument document = documentRepository.findByDocumentNumber(number).orElse(null);
        int nextVersion;
        Instant now = Instant.now();

        if (document == null) {
            document = new ConstructionDocument();
            document.setId(UUID.randomUUID());
            document.setDocumentNumber(number);
            document.setDocumentType(extracted.documentType());
            document.setTitle("Документ " + number);
            document.setConstructionObjectId(constructionObjectId);
            document.setConstructionObjectName(null);
            document.setDocumentDate(LocalDate.now());
            document.setStatus(docStatus);
            document.setCurrentVersionNumber(1);
            document.setCreatedAt(now);
            document.setUpdatedAt(now);
            document.setCreatedByUserId(actingUserId);
            document.setExtractedMetadata(extracted.metadata());
            document = documentRepository.save(document);
            nextVersion = 1;
        } else {
            if (versionMode == VersionUploadMode.NEW_VERSION || versionMode == null) {
                nextVersion = document.getCurrentVersionNumber() + 1;
            } else {
                // ATTACH_AS_VERSION_WITHOUT_BUMP — скелет: всё равно увеличиваем номер файла-версии, уточните бизнес-правило
                nextVersion = document.getCurrentVersionNumber() + 1;
            }
            document.setCurrentVersionNumber(nextVersion);
            document.setStatus(docStatus);
            document.setUpdatedAt(now);
            document.getExtractedMetadata().putAll(extracted.metadata());
            document = documentRepository.save(document);
        }

        DocumentVersionEntity ver = new DocumentVersionEntity();
        ver.setId(UUID.randomUUID());
        ver.setDocument(document);
        ver.setVersionNumber(nextVersion);
        ver.setCreatedAt(now);
        ver.setCreatedByUserId(actingUserId);
        ver.setValidationStatus(docStatus);
        ver.setFileName(Optional.ofNullable(file.getOriginalFilename()).orElse("document.xml"));
        ver.setFileSizeBytes(xmlBytes.length);
        ver.setXmlContent(xmlBytes);
        versionRepository.save(ver);

        DocumentEventEntity ev = new DocumentEventEntity();
        ev.setId(UUID.randomUUID());
        ev.setDocument(document);
        ev.setOccurredAt(now);
        ev.setEventType(AuditEventType.UPLOAD);
        ev.setUserId(actingUserId);
        ev.setUserDisplayName(null);
        ev.setDetails("Загружена версия " + nextVersion);
        eventRepository.save(ev);

        DocumentDetail detail = documentMapper.toDetail(documentRepository.findById(document.getId()).orElseThrow());
        return new UploadResult(document.getId(), ver.getId(), xsdResult, detail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentVersion> listVersions(UUID documentId) {
        ConstructionDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("document", documentId));
        return versionRepository.findByDocumentOrderByVersionNumberDesc(doc).stream()
                .map(documentMapper::toVersionDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getVersionXml(UUID documentId, UUID versionId) {
        DocumentVersionEntity v = versionRepository.findByIdAndDocument_Id(versionId, documentId)
                .orElseThrow(() -> new NotFoundException("version", versionId));
        return v.getXmlContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEntry> listEvents(UUID documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new NotFoundException("document", documentId);
        }
        return eventRepository.findByDocument_IdOrderByOccurredAtDesc(documentId).stream()
                .map(this::toAuditEntry)
                .toList();
    }

    @Override
    public ValidationResult validateOnly(MultipartFile file) {
        byte[] xmlBytes;
        try {
            xmlBytes = file.getBytes();
        } catch (IOException e) {
            throw new BadRequestException("Не удалось прочитать файл: " + e.getMessage());
        }
        try {
            xmlWellformedChecker.assertWellFormed(xmlBytes);
        } catch (Exception e) {
            return new ValidationResult(ValidationStatus.INVALID, List.of(
                    new com.artwell.api.dto.ValidationMessage("ERROR", null, null, "XML не well-formed: " + e.getMessage(), null)
            ));
        }
        var extracted = metadataExtractor.extract(xmlBytes, Optional.empty());
        return xsdValidationService.validate(xmlBytes, extracted.documentType());
    }

    private com.artwell.api.dto.AuditEntry toAuditEntry(DocumentEventEntity e) {
        return new com.artwell.api.dto.AuditEntry(
                e.getId(),
                e.getOccurredAt(),
                e.getEventType(),
                e.getUserId(),
                e.getUserDisplayName(),
                e.getDocument().getId(),
                e.getDocument().getDocumentNumber(),
                e.getDetails(),
                e.getPayloadJson()
        );
    }
}
