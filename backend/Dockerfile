FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /usr/src/myapp

COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /usr/src/myapp

COPY --from=builder /usr/src/myapp/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
