FROM eclipse-temurin:22-jdk-alpine

WORKDIR /app

COPY build/libs/PujaParikrama-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 2024

ENTRYPOINT ["java", "-jar", "app.jar"]