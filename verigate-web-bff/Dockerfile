FROM amazoncorretto:21 AS build
RUN yum install -y tar gzip
ARG MAVEN_VERSION=3.9.9
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz | tar xz -C /opt \
    && ln -s /opt/apache-maven-${MAVEN_VERSION}/bin/mvn /usr/bin/mvn
WORKDIR /app
ARG GITHUB_TOKEN
RUN mkdir -p /root/.m2 && \
    echo '<settings><servers><server><id>github</id><username>x-access-token</username><password>'${GITHUB_TOKEN}'</password></server></servers></settings>' > /root/.m2/settings.xml
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests -q

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
