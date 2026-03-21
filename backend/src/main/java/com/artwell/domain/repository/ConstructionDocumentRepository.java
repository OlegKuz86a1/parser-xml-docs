package com.artwell.domain.repository;

import com.artwell.api.enums.DocumentTypeCode;
import com.artwell.api.enums.ValidationStatus;
import com.artwell.domain.entity.ConstructionDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ConstructionDocumentRepository extends JpaRepository<ConstructionDocument, UUID> {

    long countByStatus(ValidationStatus status);

    @Query("SELECT COUNT(DISTINCT d.constructionObjectId) FROM ConstructionDocument d WHERE d.constructionObjectId IS NOT NULL")
    long countDistinctConstructionObjects();

    Optional<ConstructionDocument> findByDocumentNumber(String documentNumber);

    @Query("""
            SELECT d FROM ConstructionDocument d
            WHERE (:status IS NULL OR d.status = :status)
            AND (:docType IS NULL OR d.documentType = :docType)
            AND (:objectId IS NULL OR d.constructionObjectId = :objectId)
            AND (:num IS NULL OR :num = '' OR LOWER(d.documentNumber) LIKE LOWER(CONCAT('%', :num, '%')))
            """)
    Page<ConstructionDocument> search(
            @Param("status") ValidationStatus status,
            @Param("docType") DocumentTypeCode documentType,
            @Param("objectId") UUID constructionObjectId,
            @Param("num") String documentNumber,
            Pageable pageable
    );
}
