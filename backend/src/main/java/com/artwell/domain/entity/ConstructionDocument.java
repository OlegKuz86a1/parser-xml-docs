package com.artwell.domain.entity;

import com.artwell.api.enums.DocumentTypeCode;
import com.artwell.api.enums.ValidationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
public class ConstructionDocument {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String documentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentTypeCode documentType;

    private String title;

    private String constructionObjectName;

    private UUID constructionObjectId;

    private LocalDate documentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationStatus status;

    @Column(nullable = false)
    private int currentVersionNumber;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private UUID createdByUserId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> extractedMetadata = new HashMap<>();
}
