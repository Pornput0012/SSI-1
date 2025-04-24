FROM maven:3.8.6 AS builder
WORKDIR /usr/src/myapp

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /usr/src/myapp

COPY --from=builder /usr/src/myapp/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
