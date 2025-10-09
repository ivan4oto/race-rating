# Stage 1: Build Angular app
FROM node:20 AS build
WORKDIR /app

COPY race-rating-ui/package*.json .
RUN npm ci

COPY /race-rating-ui .
RUN npm run build -- --configuration e2e

# Stage 2: Serve with Nginx
FROM nginx:alpine
RUN apk add --no-cache openssl && \
mkdir -p /etc/nginx/certs && \
openssl req -x509 -nodes -newkey rsa:2048 -days 365 \
-subj "/CN=ui" \
-keyout /etc/nginx/certs/ui.key \
-out /etc/nginx/certs/ui.crt
COPY --from=build /app/dist/race-rating-ui/browser /usr/share/nginx/html
COPY docker/e2e/ui-ssl.conf /etc/nginx/conf.d/default.conf
EXPOSE 443
