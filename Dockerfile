FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/courier-tracking-api-0.0.1-SNAPSHOT.jar app.jar

COPY src/main/resources/stores.json /app/stores.json

ENTRYPOINT ["/bin/bash", "-c", "java -jar app.jar"]