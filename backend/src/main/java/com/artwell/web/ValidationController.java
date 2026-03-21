package com.artwell.web;

import com.artwell.api.dto.ValidationResult;
import com.artwell.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/validation")
@RequiredArgsConstructor
public class ValidationController {

    private final DocumentService documentService;

    @PostMapping(value = "/validate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ValidationResult validate(@RequestParam("file") MultipartFile file) {
        return documentService.validateOnly(file);
    }
}
