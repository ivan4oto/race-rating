# Stage 1: Build Angular app
FROM node:20 AS build
WORKDIR /app

COPY race-rating-ui/package*.json .
RUN npm ci

COPY /race-rating-ui .
RUN npm run build -- --configuration e2e

# Stage 2: Serve with Nginx
FROM nginx:alpine
COPY --from=build /app/dist/race-rating-ui/browser /usr/share/nginx/html

EXPOSE 80
