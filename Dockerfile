FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache nginx netcat-openbsd

COPY frontend/dist /usr/share/nginx/html
COPY deploy/nginx/default.conf /etc/nginx/conf.d/default.conf

RUN mkdir -p /var/lib/yqmetals/uploads /var/log/yqmetals /app /run/nginx

# Build inputs (produced by the steps that run before `docker build` in
# .github/workflows/build.yml):
#   frontend/dist/                        <- npm run build
#   backend/target/yqmetals-backend-1.0.0.jar  <- mvn package
# Local build: run `npm run build` and `mvn -B -DskipTests package` first,
# then `docker build -t yqmetals/backend .`

COPY backend/target/yqmetals-backend-*.jar /app/yqmetals-backend.jar
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

VOLUME ["/var/lib/yqmetals/uploads", "/var/log/yqmetals"]

EXPOSE 80 443 8082
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
