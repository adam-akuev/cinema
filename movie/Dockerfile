FROM openjdk:22-slim

LABEL maintainer="Adam Akuev <akuev2005@mail.ru"

ARG JAR_FILE

COPY ${JAR_FILE} app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]