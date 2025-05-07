FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/auth_server-0.0.1-SNAPSHOT.jar auth_server.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "auth_server.jar"]
