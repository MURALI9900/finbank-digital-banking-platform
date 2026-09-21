# Multi-service build image for the FinBank backend.
ARG SERVICE
FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY backend/$SERVICE/pom.xml backend/$SERVICE/pom.xml
COPY backend/$SERVICE/src backend/$SERVICE/src
RUN mvn -f backend/$SERVICE/pom.xml -DskipTests package
FROM eclipse-temurin:17-jre
ARG SERVICE
WORKDIR /app
COPY --from=build /workspace/backend/$SERVICE/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
