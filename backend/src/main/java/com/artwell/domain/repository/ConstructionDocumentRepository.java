package com.artwell.domain.repository;

import com.artwell.api.enums.ValidationStatus;
import com.artwell.domain.entity.ConstructionDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ConstructionDocumentRepository extends JpaRepository<ConstructionDocument, UUID> {

    long countByStatus(ValidationStatus status);

    @Query("SELECT COUNT(DISTINCT d.constructionObjectId) FROM ConstructionDocument d WHERE d.constructionObjectId IS NOT NULL")
    long countDistinctConstructionObjects();

    Optional<ConstructionDocument> findByDocumentNumber(String documentNumber);
}
