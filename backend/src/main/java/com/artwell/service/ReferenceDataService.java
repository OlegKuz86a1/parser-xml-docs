package com.artwell.service;

import com.artwell.api.dto.ConstructionObject;
import com.artwell.api.dto.DocumentTypeInfo;
import com.artwell.api.enums.DocumentTypeCode;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Service
public class ReferenceDataService {

    public List<DocumentTypeInfo> documentTypes() {
        return Arrays.stream(DocumentTypeCode.values())
                .filter(c -> c != DocumentTypeCode.OTHER)
                .map(this::info)
                .toList();
    }

    private DocumentTypeInfo info(DocumentTypeCode code) {
        return new DocumentTypeInfo(
                code,
                titleRu(code),
                URI.create("https://www.minstroyrf.gov.ru/tim/xml-skhemy/")
        );
    }

    private String titleRu(DocumentTypeCode code) {
        return switch (code) {
            case AXIS_LAYOUT_ACT -> "Акт разбивки осей ОКС на местности";
            case ENGINEERING_NETWORKS_ACT -> "Акт освидетельствования участков сетей ИТО";
            case HIDDEN_WORKS_ACT -> "Акт освидетельствования скрытых работ";
            case STRUCTURES_ACT -> "Акт освидетельствования ответственных конструкций";
            case GEODESY_BASE_ACT -> "Акт освидетельствования геодезической разбивочной основы";
            case EQUIPMENT_DEFECTS_ACT -> "Акт о выявленных дефектах приборов и оборудования";
            case HYDROPNEUMATIC_TEST_ACT -> "Акт испытания гидропневматической ёмкости";
            case FIRE_WATER_TEST_ACT -> "Акт испытания внутреннего противопожарного водопровода";
            case INCOMING_CONTROL_ACT -> "Акт о проведении входного контроля";
            case CONCRETE_JOURNAL -> "Журнал бетонных работ";
            case AUTHOR_SUPERVISION_JOURNAL -> "Журнал авторского надзора";
            case GENERAL_WORK_LOG -> "Общий журнал работ";
            case MATERIALS_INCOMING_JOURNAL -> "Журнал входного контроля материалов";
            case INSPECTION_PROTOCOL -> "Протокол осмотра";
            case SUPERVISION_ACT_NO_CONTACT -> "Акт по результатам контрольного мероприятия без взаимодействия";
            case EXTENSION_DECISION -> "Решение органа о продлении срока предписания";
            case DESKTOP_INSPECTION_ACT -> "Акт документарной внеплановой проверки";
            case FIELD_INSPECTION_ACT -> "Акт выездной внеплановой проверки";
            case VIOLATION_ORDER -> "Предписание об устранении нарушений";
            case VIOLATION_REMEDIATION_NOTICE -> "Извещение об устранении нарушений";
            case SUPERVISION_DECISION -> "Решение о проведении контрольного мероприятия";
            default -> code.name();
        };
    }

    /** Заглушка: пустой список или из профиля пользователя — расширить при появлении сущности «объект». */
    public List<ConstructionObject> constructionObjects() {
        return List.of();
    }
}
