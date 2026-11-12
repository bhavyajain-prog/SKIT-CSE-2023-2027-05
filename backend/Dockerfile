# --- Build stage ---
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Resolve dependencies first so they stay cached until pom.xml changes.
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw -B -ntp dependency:go-offline

COPY src src
# Tests need PostgreSQL and run in Backend CI, so the image build skips them.
RUN ./mvnw -B -ntp package -DskipTests

# --- Runtime stage ---
FROM eclipse-temurin:25-jre
WORKDIR /app

RUN groupadd --system kronos && useradd --system --gid kronos kronos
COPY --from=build /app/target/*.jar app.jar
USER kronos

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
