FROM mcr.microsoft.com/playwright/java:v1.46.0-jammy
WORKDIR /app

# Copy Maven wrapper from root
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Copy only POM files first for dependency cache
COPY pom.xml .
COPY race-rating-api/pom.xml race-rating-api/
COPY e2e-tests/pom.xml e2e-tests/

# Pre-fetch dependencies
RUN ./mvnw -q -B -pl e2e-tests -am dependency:go-offline

# Copy full project source
COPY . .

ENV baseUrl=http://ui:80

CMD bash -lc "\
  ./mvnw -q -B -pl e2e-tests test -DbaseUrl=${baseUrl} ; \
  mkdir -p /artifacts && cp -r e2e-tests/target/surefire-reports /artifacts/ 2>/dev/null || true"
