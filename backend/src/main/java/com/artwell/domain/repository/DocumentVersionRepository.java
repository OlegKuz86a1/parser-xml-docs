package com.artwell.domain.repository;

import com.artwell.domain.entity.ConstructionDocument;
import com.artwell.domain.entity.DocumentVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersionEntity, UUID> {

    List<DocumentVersionEntity> findByDocumentOrderByVersionNumberDesc(ConstructionDocument document);

    Optional<DocumentVersionEntity> findByIdAndDocument_Id(UUID versionId, UUID documentId);
}
