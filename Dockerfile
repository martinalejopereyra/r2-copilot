FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

ENV GRADLE_OPTS="-Dhttps.protocols=TLSv1.2,TLSv1.3 -Dorg.gradle.daemon=false"

COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew && ./gradlew --version --no-daemon --configuration-cache

COPY src/ src/

RUN ls -R src/main/resources/db

RUN ./gradlew clean processResources bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN adduser --system --group spring
USER spring

COPY --from=build /app/build/libs/*.jar app.jar

ENV JAVA_OPTS="-XX:+UseZGC"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
