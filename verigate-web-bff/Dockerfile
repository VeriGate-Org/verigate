FROM amazoncorretto:21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN yum install -y maven && mvn package -DskipTests -q

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
