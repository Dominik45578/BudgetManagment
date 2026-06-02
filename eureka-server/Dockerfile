## Dockerfile.eureka
#FROM eclipse-temurin:21-jdk-alpine AS build
#WORKDIR /app
#COPY .mvn .mvn
#COPY mvnw pom.xml ./
#COPY eureka-server/pom.xml eureka-server/
#RUN chmod +x mvnw && ./mvnw dependency:go-offline -pl eureka-server -am -B
#COPY eureka-server/src eureka-server/src
#RUN ./mvnw package -pl eureka-server -am -DskipTests -B

#FROM eclipse-temurin:21-jre-alpine
#WORKDIR /app
#COPY --from=build /app/eureka-server/target/*.jar app.jar
#EXPOSE 8761
#ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY eureka-server/target/eureka-server-0.0.1.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]