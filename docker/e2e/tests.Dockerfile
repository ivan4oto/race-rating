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

ENV baseUrl=https://ui

#CMD ["bash","-lc","./run-e2e.sh"]
CMD bash -lc '\
  ./mvnw -B -pl e2e-tests -DbaseUrl="$baseUrl" -Dmaven.test.failure.ignore=true test && \
  ./mvnw -B -pl e2e-tests org.apache.maven.plugins:maven-surefire-report-plugin:3.2.5:report-only && \
  mkdir -p /artifacts /artifacts/site && \
  cp -r e2e-tests/target/surefire-reports /artifacts/ 2>/dev/null || true; \
  cp -r e2e-tests/target/site/. /artifacts/site 2>/dev/null || true'
