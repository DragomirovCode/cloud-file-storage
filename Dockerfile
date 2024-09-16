FROM openjdk:17-alpine

WORKDIR /app

EXPOSE 8080

COPY /target/ROOT.jar /app/ROOT.jar

# Устанавливаем команду для запуска JAR файла
ENTRYPOINT ["java", "-jar", "/app/ROOT.jar", "--spring.profiles.active=docker"]
