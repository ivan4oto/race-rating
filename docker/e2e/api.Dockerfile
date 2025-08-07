# Stage 1: Build Spring Boot app
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy wrapper from root (now that you have a root wrapper)
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Copy only what’s needed for the api module build
COPY pom.xml ./
COPY race-rating-api/pom.xml race-rating-api/

# Optional: prefetch deps for faster builds
RUN ./mvnw -q -B -f race-rating-api/pom.xml dependency:go-offline

# Now copy the api sources
COPY race-rating-api/ race-rating-api/

# Build ONLY the api module (no reactor scan of other modules)
RUN ./mvnw -q -B -f race-rating-api/pom.xml -DskipTests package

# Stage 2: Run Spring Boot app
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/race-rating-api/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
