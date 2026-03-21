package com.artwell.service;

import com.artwell.api.dto.ValidationMessage;
import com.artwell.api.dto.ValidationResult;
import com.artwell.api.enums.DocumentTypeCode;
import com.artwell.api.enums.ValidationStatus;
import com.artwell.config.ArtwellXsdProperties;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Частичная XSD-валидация: если для типа документа задан classpath к XSD — проверка по схеме,
 * иначе возвращается VALID с предупреждением (подключите схемы Минстроя в {@code artwell.xsd.schema-map}).
 */
@Service
public class XsdValidationService {

    private final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    private final ArtwellXsdProperties xsdProperties;

    public XsdValidationService(ArtwellXsdProperties xsdProperties) {
        this.xsdProperties = xsdProperties;
    }

    public ValidationResult validate(byte[] xmlBytes, DocumentTypeCode documentType) {
        Map<String, String> schemaMap = xsdProperties.getSchemaMap();
        String location = schemaMap != null ? schemaMap.get(documentType.name()) : null;
        List<ValidationMessage> messages = new ArrayList<>();

        if (location == null || location.isBlank()) {
            messages.add(new ValidationMessage(
                    "WARNING",
                    null,
                    null,
                    "XSD для типа " + documentType + " не настроена (artwell.xsd.schema-map)",
                    null
            ));
            return new ValidationResult(ValidationStatus.VALID, messages);
        }

        try {
            var res = new org.springframework.core.io.ClassPathResource(location);
            if (!res.exists()) {
                messages.add(new ValidationMessage("ERROR", null, null, "Файл схемы не найден: " + location, null));
                return new ValidationResult(ValidationStatus.INVALID, messages);
            }
            Schema schema = schemaFactory.newSchema(res.getURL());
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new ByteArrayInputStream(xmlBytes)));
            return new ValidationResult(ValidationStatus.VALID, List.of());
        } catch (SAXException e) {
            messages.add(new ValidationMessage(
                    "ERROR",
                    null,
                    null,
                    Optional.ofNullable(e.getMessage()).orElse("XSD validation failed"),
                    null
            ));
            return new ValidationResult(ValidationStatus.INVALID, messages);
        } catch (IOException e) {
            messages.add(new ValidationMessage("ERROR", null, null, e.getMessage(), null));
            return new ValidationResult(ValidationStatus.INVALID, messages);
        }
    }
}
