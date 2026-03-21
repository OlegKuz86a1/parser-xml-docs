package com.artwell.web;

import com.artwell.api.dto.ConstructionObject;
import com.artwell.api.dto.DocumentTypeInfo;
import com.artwell.service.ReferenceDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reference")
@RequiredArgsConstructor
public class ReferenceController {

    private final ReferenceDataService referenceDataService;

    @GetMapping("/document-types")
    public List<DocumentTypeInfo> documentTypes() {
        return referenceDataService.documentTypes();
    }

    @GetMapping("/construction-objects")
    public List<ConstructionObject> constructionObjects() {
        return referenceDataService.constructionObjects();
    }
}
