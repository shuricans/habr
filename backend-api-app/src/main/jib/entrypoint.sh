#!/bin/sh

# Abort on any error (including if wait-for fails).
set -e

args=""

# Set specific profile for cloud-config service if exist.
if [ -n "$PROFILE" ]; then
  args="$args --spring.profiles.active=$PROFILE"
fi

# Waiting db if exist.
if [ -n "$DB_HOST" ] && [ -n "$DB_PORT" ]; then
  /wait-for-service.sh "$DB_HOST" "$DB_PORT"
fi

exec java -cp \
  $(cat /app/jib-classpath-file) \
  $(cat /app/jib-main-class-file) \
  $args