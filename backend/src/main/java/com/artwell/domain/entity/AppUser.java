package com.artwell.domain.entity;

import com.artwell.api.enums.DocumentTypeCode;
import com.artwell.api.enums.UserRole;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
public class AppUser {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String displayName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "app_user_allowed_doc_types", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type")
    private Set<DocumentTypeCode> allowedDocumentTypes = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "app_user_objects", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "object_id")
    private Set<UUID> constructionObjectIds = new HashSet<>();
}
