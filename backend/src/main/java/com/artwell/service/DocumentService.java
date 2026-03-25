package com.artwell.service;

import com.artwell.api.dto.DocumentDetail;
import com.artwell.api.dto.DocumentVersion;
import com.artwell.api.dto.PageDocumentSummary;
import com.artwell.api.dto.UploadResult;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface DocumentService {

    PageDocumentSummary listDocuments(Pageable pageable);

    DocumentDetail getDocument(UUID id);

    byte[] getCurrentXml(UUID documentId);

    UploadResult upload(
            MultipartFile file,
            String documentNumberQuery,
            UUID constructionObjectId,
            UUID actingUserId
    );

    List<DocumentVersion> listVersions(UUID documentId);
}
