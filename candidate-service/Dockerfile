FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/candidate-service.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]