package com.artwell.domain.repository;

import com.artwell.domain.entity.DocumentEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentEventRepository extends JpaRepository<DocumentEventEntity, UUID> {
}
