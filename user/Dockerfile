FROM openjdk:22-jdk-slim

LABEL maintainer="Adam Akuev <akuev2005@mail.ru"

COPY target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app.jar"]