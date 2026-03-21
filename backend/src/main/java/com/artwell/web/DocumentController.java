package com.artwell.web;

import com.artwell.api.dto.DocumentDetail;
import com.artwell.api.dto.AuditEntry;
import com.artwell.api.dto.DocumentVersion;
import com.artwell.api.dto.PageDocumentSummary;
import com.artwell.api.dto.UploadResult;
import com.artwell.api.enums.DocumentTypeCode;
import com.artwell.api.enums.ValidationStatus;
import com.artwell.api.enums.VersionUploadMode;
import com.artwell.service.ActingUserService;
import com.artwell.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final ActingUserService actingUserService;

    @GetMapping
    public PageDocumentSummary list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) ValidationStatus status,
            @RequestParam(required = false) DocumentTypeCode documentType,
            @RequestParam(required = false) UUID constructionObjectId,
            @RequestParam(required = false) String documentNumber
    ) {
        Pageable p = PageRequest.of(page, size);
        return documentService.listDocuments(status, documentType, constructionObjectId, documentNumber, p);
    }

    @GetMapping("/{documentId}")
    public DocumentDetail get(@PathVariable UUID documentId) {
        return documentService.getDocument(documentId);
    }

    @GetMapping("/{documentId}/xml")
    public ResponseEntity<byte[]> getXml(@PathVariable UUID documentId) {
        byte[] xml = documentService.getCurrentXml(documentId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document.xml\"")
                .body(xml);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResult> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String documentNumber,
            @RequestParam(required = false) VersionUploadMode versionMode,
            @RequestParam(required = false) UUID constructionObjectId
    ) {
        UploadResult result = documentService.upload(
                file,
                documentNumber,
                versionMode,
                constructionObjectId,
                actingUserService.requireUserId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{documentId}/versions")
    public List<DocumentVersion> versions(@PathVariable UUID documentId) {
        return documentService.listVersions(documentId);
    }

    @GetMapping("/{documentId}/versions/{versionId}/xml")
    public ResponseEntity<byte[]> versionXml(
            @PathVariable UUID documentId,
            @PathVariable UUID versionId
    ) {
        byte[] xml = documentService.getVersionXml(documentId, versionId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document-v.xml\"")
                .body(xml);
    }

    @GetMapping("/{documentId}/events")
    public List<AuditEntry> events(@PathVariable UUID documentId) {
        return documentService.listEvents(documentId);
    }
}
