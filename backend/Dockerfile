FROM maven:latest AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:22
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
