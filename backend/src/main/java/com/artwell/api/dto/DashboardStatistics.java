package com.artwell.api.dto;

public record DashboardStatistics(
        long totalDocuments,
        long validDocuments,
        long invalidDocuments,
        long pendingDocuments,
        long constructionObjectsCount
) {}
