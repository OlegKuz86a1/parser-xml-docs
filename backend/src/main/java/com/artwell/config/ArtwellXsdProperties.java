package com.artwell.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "artwell.xsd")
@Getter
@Setter
public class ArtwellXsdProperties {

    /** Ключ: имя {@link com.artwell.api.enums.DocumentTypeCode}, значение: classpath XSD, например schema/minstroy/example.xsd */
    private Map<String, String> schemaMap = new HashMap<>();
}
