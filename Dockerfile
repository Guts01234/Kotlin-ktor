# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY settings.gradle.kts settings.gradle.kts
COPY build.gradle.kts build.gradle.kts
COPY gradle.properties gradle.properties
COPY gradle/libs.versions.toml gradle/libs.versions.toml
COPY src src

RUN chmod +x gradlew \
    && ./gradlew installDist --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

RUN apk add --no-cache wget \
    && addgroup -S app \
    && adduser -S app -G app
USER app

COPY --from=build /app/build/install/taskboard-api/ /app/

ENV PORT=8080
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8080/health || exit 1

ENTRYPOINT ["/app/bin/taskboard-api"]
