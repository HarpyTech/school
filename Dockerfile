# ─── Stage 1: Build Spring Boot Backend ───────────────────────────────────────
FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom first (layer caching for deps)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn package -Dmaven.test.skip=true -B

# ─── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Create non-root user for security
RUN addgroup -S schoolapp && adduser -S schoolapp -G schoolapp

# Copy the built jar
COPY --from=build /app/target/*.jar app.jar

# Change ownership
RUN chown -R schoolapp:schoolapp /app

USER schoolapp

# Application port (Cloud Run expects 8080 by default)
EXPOSE 8080

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.backgroundpreinitializer.ignore=true"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
