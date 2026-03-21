# Сборка: из корня backend: mvn -DskipTests package
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/artwell-api-0.1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
