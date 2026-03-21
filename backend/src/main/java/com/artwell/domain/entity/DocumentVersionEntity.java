package com.artwell.domain.entity;

import com.artwell.api.enums.ValidationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "document_versions")
@Getter
@Setter
@NoArgsConstructor
public class DocumentVersionEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private ConstructionDocument document;

    @Column(nullable = false)
    private int versionNumber;

    @Column(nullable = false)
    private Instant createdAt;

    private UUID createdByUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidationStatus validationStatus;

    private String fileName;

    private long fileSizeBytes;

    @Lob
    @Column(nullable = false, columnDefinition = "bytea")
    private byte[] xmlContent;
}
