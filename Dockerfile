FROM ubuntu:latest
LABEL authors="Tiwar"

ENTRYPOINT ["top", "-b"]

FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/crm-0.0.1-SNAPSHOT.jar"]