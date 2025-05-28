# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /usr/app
COPY gradlew gradlew.bat settings.gradle build.gradle gradle.properties ./
COPY gradle ./gradle
COPY src ./src
RUN chmod +x gradlew && ./gradlew clean build

# Runtime stage (Khi run time thì chỉ cop những file cần thiết để chạy trong quarkus-app thôi)
FROM eclipse-temurin:21-jre
ENV LANGUAGE='en_US:en'

COPY --from=build --chown=185 /usr/app/build/quarkus-app/lib/ /deployments/lib/
COPY --from=build --chown=185 /usr/app/build/quarkus-app/*.jar /deployments/
COPY --from=build --chown=185 /usr/app/build/quarkus-app/app/ /deployments/app/
COPY --from=build --chown=185 /usr/app/build/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT [ "java", "-jar", "/deployments/quarkus-run.jar" ]