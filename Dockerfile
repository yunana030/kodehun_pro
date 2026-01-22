# Build stage
FROM gradle:jdk21-jammy AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew build --no-daemon
LABEL org.name="yuna"

# Run stage
FROM openjdk:21-slim
COPY --from=build /home/gradle/src/build/libs/kodehun-pro-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
