FROM openjdk:22-slim

LABEL maintainer="Adam Akuev <akuev2005@mail.ru"

COPY target/*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "/app.jar"]