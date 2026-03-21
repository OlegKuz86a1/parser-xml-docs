# API First → Spring Boot

Исходный контракт: **`artwell-api-openapi-3.0.yaml`** (папка `Example XSD`).

Скелет Spring Boot 3 + Java 17: каталог **`/backend`** (`README.md` внутри), `mvn spring-boot:run` при запущенном PostgreSQL.

## Вариант 1: springdoc-openapi + ручные контроллеры

1. Реализуйте REST-контроллеры вручную, соблюдая пути и модели из YAML.
2. Подключите [springdoc-openapi](https://springdoc.org/) и укажите путь к файлу или сгенерируйте документацию из аннотаций, синхронизированных с YAML.

Зависимости (Maven):

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.6.0</version>
</dependency>
```

## Вариант 2: openapi-generator (заготовки API + DTO)

```bash
openapi-generator-cli generate \
  -i artwell-api-openapi-3.0.yaml \
  -g spring-boot \
  -o generated-server \
  --additional-properties=interfaceOnly=true,useSpringBoot3=true,dateLibrary=java8
```

Далее переносите/адаптируйте интерфейсы `Api` и модели в свой модуль, реализуя бизнес-логику (XSD, PostgreSQL, файловое хранилище).

## Соответствие ТЗ «Описание задачи от Артвелл»

| Требование | Эндпоинты / схемы |
|------------|-------------------|
| Загрузка XML, валидация XSD | `POST /documents/upload`, `POST /validation/validate`, `ValidationResult` |
| Парсинг, ключевые поля | `DocumentDetail`, `extractedMetadata`, `Participant` |
| Хранение файла + БД | Реализация сервиса; API отдаёт `documentId`, `versionId`, ссылки на скачивание |
| Список, карточка, скачивание | `GET /documents`, `GET /documents/{id}`, `GET .../xml` |
| Версии | `VersionUploadMode`, `GET /documents/{id}/versions`, версии XML |
| История по документу (вместо глобального аудит-лога в UI) | `GET /documents/{documentId}/events`, `AuditEntry`, `AuditEventType` |
| Роли подрядчик/заказчик | `UserRole`, `GET /users/me`, `403` при отсутствии прав |
| Типы документов Минстроя | `DocumentTypeCode`, `GET /reference/document-types` |
| Сводная статистика (в UI панели нет) | `GET /statistics`, `DashboardStatistics` |

При необходимости сузьте или расширьте enum `DocumentTypeCode` под фактический маппинг XSD из репозитория схем.
