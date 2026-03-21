package com.artwell.api.dto;

import java.util.List;

public record PageDocumentSummary(
        List<DocumentSummary> content,
        long totalElements,
        int totalPages,
        int number,
        int size
) {}
