#!/bin/sh
set -eu

UPLOAD_DIR="${YQ_UPLOAD_DIR:-/var/lib/yqmetals/uploads}"
mkdir -p "$UPLOAD_DIR"

# Wait up to 60s for MySQL to accept connections. Without this, a container
# restart can bring up the backend before the database is ready, and the JVM
# exits immediately.
wait_for_db() {
    local i=0
    while [ "$i" -lt 60 ]; do
        if nc -z -w 2 "${YQ_DB_HOST:-db}" "${YQ_DB_PORT:-3306}" 2>/dev/null; then
            echo "MySQL is reachable on ${YQ_DB_HOST:-db}:${YQ_DB_PORT:-3306}"
            return 0
        fi
        sleep 1
        i=$((i + 1))
    done
    echo "ERROR: MySQL not reachable after 60s; aborting startup." >&2
    return 1
}

wait_for_db || exit 1

# prod profile 是生产默认值；compose 里也显式声明了 SPRING_PROFILES_ACTIVE: prod,
# 两者一致。直接用 docker run 时由这里兜底，防止回退到开发 profile.

# backend runs in the background; nginx is the foreground process
java ${YQ_JAVA_OPTS:--Xmx512m} -jar /app/yqmetals-backend.jar --spring.profiles.active="${SPRING_PROFILES_ACTIVE:-prod}" &
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
