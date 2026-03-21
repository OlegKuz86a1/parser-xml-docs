package com.artwell.service;

import com.artwell.api.dto.DashboardStatistics;
import com.artwell.api.enums.ValidationStatus;
import com.artwell.domain.repository.ConstructionDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ConstructionDocumentRepository documentRepository;

    public DashboardStatistics getStatistics() {
        long total = documentRepository.count();
        long valid = documentRepository.countByStatus(ValidationStatus.VALID);
        long invalid = documentRepository.countByStatus(ValidationStatus.INVALID);
        long pending = documentRepository.countByStatus(ValidationStatus.PENDING);
        long objects = documentRepository.countDistinctConstructionObjects();
        return new DashboardStatistics(total, valid, invalid, pending, objects);
    }
}
