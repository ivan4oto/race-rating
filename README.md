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