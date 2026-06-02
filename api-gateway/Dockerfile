# Dockerfile.gateway
#FROM eclipse-temurin:21-jdk-alpine AS build
#WORKDIR /app
#COPY .mvn .mvn
#COPY mvnw pom.xml ./
#COPY api-gateway/pom.xml api-gateway/
#RUN chmod +x mvnw && ./mvnw dependency:go-offline -pl api-gateway -am -B
#COPY api-gateway/src api-gateway/src
#RUN ./mvnw package -pl api-gateway -am -DskipTests -B

#FROM eclipse-temurin:21-jre-alpine
#WORKDIR /app
#COPY --from=build /app/api-gateway/target/*.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY api-gateway/target/api-gateway-0.0.1.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]