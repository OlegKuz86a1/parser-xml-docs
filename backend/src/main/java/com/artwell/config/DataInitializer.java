package com.artwell.config;

import com.artwell.api.enums.DocumentTypeCode;
import com.artwell.api.enums.UserRole;
import com.artwell.api.enums.ValidationStatus;
import com.artwell.domain.entity.AppUser;
import com.artwell.domain.entity.ConstructionDocument;
import com.artwell.domain.entity.DocumentVersionEntity;
import com.artwell.domain.repository.AppUserRepository;
import com.artwell.domain.repository.ConstructionDocumentRepository;
import com.artwell.domain.repository.DocumentVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private static final UUID DEMO_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final byte[] SAMPLE_XML_BYTES =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><demo xmlns=\"urn:demo\">seed</demo>".getBytes(StandardCharsets.UTF_8);

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConstructionDocumentRepository documentRepository;
    private final DocumentVersionRepository versionRepository;

    @Bean
    CommandLineRunner seedUsers() {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }
            AppUser u = new AppUser();
            u.setId(DEMO_USER_ID);
            u.setUsername("demo");
            u.setPasswordHash(passwordEncoder.encode("demo"));
            u.setRole(UserRole.CONTRACTOR);
            u.setDisplayName("Демо пользователь");
            u.setAllowedDocumentTypes(Set.copyOf(java.util.EnumSet.allOf(DocumentTypeCode.class)));
            u.setConstructionObjectIds(Set.of());
            userRepository.save(u);
            log.info("Создан пользователь demo / demo");
        };
    }

    /**
     * Несколько примеров документов в пустой БД (для списка и карточек в UI).
     */
    @Bean
    CommandLineRunner seedSampleDocuments() {
        return args -> {
            if (documentRepository.count() > 0) {
                return;
            }
            Instant now = Instant.now();
            seedDocument(
                    UUID.fromString("a0000001-0000-4000-8000-000000000001"),
                    "АО-2024-001",
                    DocumentTypeCode.AXIS_LAYOUT_ACT,
                    "Акт разбивки осей",
                    "ЖК «Северный»",
                    UUID.fromString("11111111-1111-1111-1111-111111111101"),
                    LocalDate.of(2024, 1, 15),
                    ValidationStatus.VALID,
                    now
            );
            seedDocument(
                    UUID.fromString("a0000002-0000-4000-8000-000000000002"),
                    "АОСР-2024-015",
                    DocumentTypeCode.HIDDEN_WORKS_ACT,
                    "Акт освидетельствования скрытых работ",
                    "ТЦ «Плаза»",
                    UUID.fromString("11111111-1111-1111-1111-111111111102"),
                    LocalDate.of(2024, 1, 14),
                    ValidationStatus.VALID,
                    now
            );
            seedDocument(
                    UUID.fromString("a0000003-0000-4000-8000-000000000003"),
                    "ЖБР-2024-003",
                    DocumentTypeCode.CONCRETE_JOURNAL,
                    "Журнал бетонных работ",
                    "Школа №15",
                    UUID.fromString("11111111-1111-1111-1111-111111111103"),
                    LocalDate.of(2024, 1, 13),
                    ValidationStatus.INVALID,
                    now
            );
            seedDocument(
                    UUID.fromString("a0000004-0000-4000-8000-000000000004"),
                    "ПО-2024-008",
                    DocumentTypeCode.INSPECTION_PROTOCOL,
                    "Протокол осмотра",
                    "ЖК «Северный»",
                    UUID.fromString("11111111-1111-1111-1111-111111111101"),
                    LocalDate.of(2024, 1, 12),
                    ValidationStatus.VALID,
                    now
            );
            log.info("Добавлены примеры документов (4 шт.)");
        };
    }

    private void seedDocument(
            UUID id,
            String number,
            DocumentTypeCode type,
            String title,
            String objectName,
            UUID objectId,
            LocalDate date,
            ValidationStatus status,
            Instant now
    ) {
        ConstructionDocument d = new ConstructionDocument();
        d.setId(id);
        d.setDocumentNumber(number);
        d.setDocumentType(type);
        d.setTitle(title);
        d.setConstructionObjectName(objectName);
        d.setConstructionObjectId(objectId);
        d.setDocumentDate(date);
        d.setStatus(status);
        d.setCurrentVersionNumber(1);
        d.setCreatedAt(now);
        d.setUpdatedAt(now);
        d.setCreatedByUserId(DEMO_USER_ID);
        d.getExtractedMetadata().put("seed", true);
        documentRepository.save(d);

        DocumentVersionEntity v = new DocumentVersionEntity();
        v.setId(UUID.randomUUID());
        v.setDocument(d);
        v.setVersionNumber(1);
        v.setCreatedAt(now);
        v.setCreatedByUserId(DEMO_USER_ID);
        v.setValidationStatus(status);
        v.setFileName(number.replace("/", "-") + ".xml");
        v.setFileSizeBytes(SAMPLE_XML_BYTES.length);
        v.setXmlContent(SAMPLE_XML_BYTES);
        versionRepository.save(v);
    }
}
