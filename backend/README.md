# Артвелл API (скелет Spring Boot 3)

Соответствует `Example XSD/artwell-api-openapi-3.0.yaml`: контроллеры, DTO, JPA-сущности PostgreSQL, заглушки бизнес-логики, частичная XSD-валидация (см. `artwell.xsd.schema-map`).

## Требования

- **JDK 17**
- **PostgreSQL** (локально или Docker)

## Запуск локально

```bash
# Поднять БД (из корня репозитория)
docker compose up -d postgres

cd backend
mvn spring-boot:run
```

- API: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/v1/openapi`

Учётная запись после сида: **`demo` / `demo`**.

## Сборка JAR

```bash
mvn -DskipTests package
java -jar target/artwell-api-0.1.0-SNAPSHOT.jar
```

## XSD

По умолчанию схемы не привязаны: валидация XSD возвращает `VALID` с предупреждением. Чтобы проверять по файлу из classpath:

```yaml
# application.yml
artwell:
  xsd:
    schema-map:
      OTHER: schema/example.xsd
```

## Что доработать

- Реальный JWT и фильтр безопасности вместо `ActingUserService` (первый пользователь в БД).
- Парсинг полей Минстроя по `DocumentTypeCode` (сейчас заглушка в `DocumentMetadataExtractor`).
- Привязка XSD из каталога `Example XSD` к enum-типам.
