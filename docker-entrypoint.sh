#!/bin/sh
set -eu

UPLOAD_DIR="${YQ_UPLOAD_DIR:-/var/lib/yqmetals/uploads}"
mkdir -p "$UPLOAD_DIR"

# backend runs in the background; nginx is the foreground process
java ${YQ_JAVA_OPTS:--Xmx512m} -jar /app/yqmetals-backend.jar &
JAVA_PID=$!

nginx -g 'daemon off;' &
NGINX_PID=$!

shutdown() {
    kill -TERM "$NGINX_PID" 2>/dev/null || true
    kill -TERM "$JAVA_PID" 2>/dev/null || true
    wait "$JAVA_PID" 2>/dev/null || true
}
trap shutdown TERM INT

wait "$NGINX_PID"
