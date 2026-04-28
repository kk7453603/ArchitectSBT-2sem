# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

# Copy all pom files for dependency resolution
COPY pom.xml .
COPY currency-rate-proto/pom.xml currency-rate-proto/
COPY currency-rate-provider/pom.xml currency-rate-provider/
COPY rate-printer/pom.xml rate-printer/

# Copy source code
COPY currency-rate-proto/src currency-rate-proto/src
COPY currency-rate-provider/src currency-rate-provider/src

# Build the application with Maven cache
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean package -pl currency-rate-provider -am -DskipTests

# Runtime stage
FROM amazoncorretto:17-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy built JAR from build stage
COPY --from=build /build/currency-rate-provider/target/*.jar app.jar

# Use non-root user
USER appuser

# Expose ports
EXPOSE 8081 9090

# Production profile
ENV SPRING_PROFILES_ACTIVE=prod

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
