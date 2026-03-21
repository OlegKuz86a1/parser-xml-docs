package com.artwell.api.dto;

import com.artwell.api.enums.DocumentTypeCode;

import java.net.URI;

public record DocumentTypeInfo(
        DocumentTypeCode code,
        String titleRu,
        URI minstroySchemaUrl
) {}
