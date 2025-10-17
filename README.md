# Race Rating Web Application

## Overview
Race Rating is a web application where users can find and rate various races, providing valuable insights and feedback for both participants and organizers. It features an average rating system, detailed category ratings, and user feedback for each race. The app uses OAuth2 authentication with social login options for Google and Facebook.

## Features
- **Find Races**: Discover races along with their average ratings and details.
- **Rate Races**: Submit your ratings and feedback for different race categories.
- **User Feedback**: Leave positive and negative feedback on races.
- **OAuth2 Authentication**: Secure login through Google and Facebook integration.

## Technology Stack
- **Frontend**: Angular (located in `race-rating-ui/` directory)
- **Backend**: Spring Boot (located in `race-rating-api/` directory)
- **Database**: Managed via Docker Compose
- **Authentication**: OAuth2 with Google and Facebook

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Node.js and npm
- Google and Facebook Developer Accounts for OAuth2 keys

### Installation
1. **Clone the Repository**
   ```sh
   git clone https://github.com/yourusername/race-rating-app.git
   cd race-rating-app
2. **Run PostgreSQL Database from docker image**
   ```sh
   docker-compose up -d
   ```
2. **Run the Spring backend (Tomcat server)**
   ```sh
   cd race-rating-api/
   mvn spring-boot:run
   ```
3. **Setup Frontend**
    ```sh
    cd race-rating-ui/
    npm install
    ng run server
   ```
4. **Export your Google and Facebook OAuth2 credentials**
   ```sh
   export GOOGLE_CLIENT_ID=your_google_client_id
   export GOOGLE_CLIENT_SECRET=your_google_client_secret
    ```
Note: These environment variables are essential for the authentication to work correctly.

### Running the Application
After installation, the application should be running and accessible. Unauthenticated users can access public endpoints, while authenticated users can enjoy the full range of features.

## CI: E2E PR Workflow

The repository includes a GitHub Actions workflow that runs the end‑to‑end (E2E) stack with Docker Compose.

How it triggers
- Pull requests: Runs only when the PR explicitly asks for it by either:
  - Adding the label `run-e2e`, or
  - Including the keyword `[e2e]` in the PR title or description.
- Manual: Can be started from the Actions tab → "E2E PR" → "Run workflow".

Where to find results
- Job: Actions → the specific workflow run → job `e2e`.
- Artifacts: Download `e2e-artifacts` for test reports and `compose.logs.txt`.

Notes
- Concurrency: Newer pushes to the same PR cancel any in‑progress E2E runs.
- Permissions: The workflow uses read‑only repo permissions and does not require repository secrets.
- Label setup: If the `run-e2e` label doesn’t exist, create it under GitHub → Repository → Issues → Labels, or add it when editing the PR.

Run E2E locally (optional)
```sh
docker compose -f docker-compose.e2e.yaml build
docker compose -f docker-compose.e2e.yaml up --abort-on-container-exit --exit-code-from e2e-tests
# When done:
docker compose -f docker-compose.e2e.yaml down -v
```
Artifacts (reports and logs) are written to `e2e-artifacts/` and `compose.logs.txt` locally as well.

## CI: E2E PR Workflow

The repository includes a GitHub Actions workflow that runs the end‑to‑end (E2E) stack with Docker Compose.

How it triggers
- Pull requests: Runs only when the PR explicitly asks for it by either:
  - Adding the label `run-e2e`, or
  - Including the keyword `[e2e]` in the PR title or description.
- Manual: Can be started from the Actions tab → "E2E PR" → "Run workflow".

Where to find results
- Job: Actions → the specific workflow run → job `e2e`.
- Artifacts: Download `e2e-artifacts` for test reports and `compose.logs.txt`.

Notes
- Concurrency: Newer pushes to the same PR cancel any in‑progress E2E runs.
- Permissions: The workflow uses read‑only repo permissions and does not require repository secrets.
- Label setup: If the `run-e2e` label doesn’t exist, create it under GitHub → Repository → Issues → Labels, or add it when editing the PR.

Run E2E locally (optional)
```sh
docker compose -f docker-compose.e2e.yaml build
docker compose -f docker-compose.e2e.yaml up --abort-on-container-exit --exit-code-from e2e-tests
# When done:
docker compose -f docker-compose.e2e.yaml down -v
```
Artifacts (reports and logs) are written to `e2e-artifacts/` and `compose.logs.txt` locally as well.

## E2E Tests (Docker Compose)

Run the full end-to-end suite (API + UI + Localstack + Playwright tests) with Docker Compose. This will build the services, start the stack, run the tests, and collect reports on your host.

### Prerequisites
- Docker and Docker Compose

### Run tests
1. From the repo root, run:
   ```sh
   docker compose -f docker-compose.e2e.yaml up --build --abort-on-container-exit --exit-code-from e2e-tests
   ```
   - Ports used: `8080` (API), `4200` (UI), `4566` (Localstack). Ensure they are free.
   - You can add `--renew-anon-volumes` to avoid postgres volume persistance. Errors might occur if data is not cleaned after test runs.
2. Watch the test logs (optional):
   ```sh
   docker compose -f docker-compose.e2e.yaml logs -f e2e-tests
   ```

3. View results (artifacts copied from the tests container to the host):
   - Text/XML (raw Surefire): `e2e-artifacts/surefire-reports`
   - HTML report: open `e2e-artifacts/site/surefire-report.html`
     - macOS: `open e2e-artifacts/site/surefire-report.html`
     - Linux: `xdg-open e2e-artifacts/site/surefire-report.html`
     - Windows (WSL): `wslview e2e-artifacts/site/surefire-report.html`
   - Quick checks:
     ```sh
     ls e2e-artifacts/surefire-reports
     cat e2e-artifacts/surefire-reports/*.txt
     ```

4. Tear down containers when done:
   ```sh
   docker compose -f docker-compose.e2e.yaml down -v
   ```

### Notes
- The tests container runs with `baseUrl=http://ui:80` and waits for dependent services (`db`, `api`, `ui`) via Compose dependencies and health checks.
- HTML report is generated by the Maven Surefire Report plugin and stored under `e2e-artifacts/site/surefire-report.html`.
- If `e2e-artifacts/` is empty, confirm you ran with `docker-compose.e2e.yaml` and check container logs for `e2e-tests`.
