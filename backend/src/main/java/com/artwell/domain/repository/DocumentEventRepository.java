package com.artwell.domain.repository;

import com.artwell.domain.entity.ConstructionDocument;
import com.artwell.domain.entity.DocumentEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentEventRepository extends JpaRepository<DocumentEventEntity, UUID> {

    List<DocumentEventEntity> findByDocument_IdOrderByOccurredAtDesc(UUID documentId);
}
