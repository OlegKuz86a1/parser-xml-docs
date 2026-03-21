package com.artwell.service;

import com.artwell.api.enums.DocumentTypeCode;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Заглушка извлечения метаданных из XML. Заменить реальным парсингом по типам Минстроя (JAXB/XPath).
 */
@Component
public class DocumentMetadataExtractor {

    public record Extracted(String documentNumber, DocumentTypeCode documentType, Map<String, Object> metadata) {}

    public Extracted extract(byte[] xml, Optional<String> explicitNumber) {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            var builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml));
            Element root = doc.getDocumentElement();
            String num = explicitNumber.filter(s -> !s.isBlank())
                    .orElseGet(() -> Optional.ofNullable(root.getLocalName()).orElse("DOC") + "-" + UUID.randomUUID().toString().substring(0, 8));
            Map<String, Object> meta = new HashMap<>();
            meta.put("rootLocalName", root.getLocalName());
            meta.put("namespaceUri", root.getNamespaceURI());
            return new Extracted(num, DocumentTypeCode.OTHER, meta);
        } catch (Exception e) {
            String num = explicitNumber.orElse("UNKNOWN-" + UUID.randomUUID().toString().substring(0, 8));
            return new Extracted(num, DocumentTypeCode.OTHER, Map.of("parseError", e.getMessage()));
        }
    }
}
