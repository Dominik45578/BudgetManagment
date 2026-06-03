## Dockerfile.budget
#FROM eclipse-temurin:21-jdk-alpine AS build
#WORKDIR /app
#COPY .mvn .mvn
#COPY mvnw pom.xml ./
#COPY budget-service/pom.xml budget-service/
#RUN chmod +x mvnw && ./mvnw dependency:go-offline -pl budget-service -am -B
#COPY budget-service/src budget-service/src
#RUN ./mvnw package -pl budget-service -am -DskipTests -B
#
#FROM eclipse-temurin:21-jre-alpine
#WORKDIR /app
#COPY --from=build /app/budget-service/target/*.jar app.jar
#EXPOSE 8081
#ENTRYPOINT ["java", "-jar", "app.jar"]
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY budget-service/target/budget-service-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]