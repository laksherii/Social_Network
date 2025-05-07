FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/resource_server-0.0.1-SNAPSHOT.jar resource_server.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "resource_server.jar"]