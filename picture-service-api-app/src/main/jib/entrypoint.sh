#!/bin/sh

# Abort on any error (including if wait-for fails).
set -e

args=""

# Set profile if exist.
if [ -n "$PROFILE" ]; then
  args="$args --spring.profiles.active=$PROFILE"
fi

# Setting a database username and password, if provided
if [ -n "$DB_USERNAME" ] && [ -n "$DB_PASSWORD" ]; then
  args="$args --spring.datasource.username=$DB_USERNAME"
  args="$args --spring.datasource.password=$DB_PASSWORD"
fi

# Set datasource url & wait
if [ -n "$DB_HOST" ] && [ -n "$DB_PORT" ]; then
  args="$args --spring.datasource.url=jdbc:postgresql://$DB_HOST:$DB_PORT/habr"
  # Waiting database container if exist.
  /wait-for-service.sh "$DB_HOST" "$DB_PORT"
fi

exec java -cp \
  $(cat /app/jib-classpath-file) \
  $(cat /app/jib-main-class-file) \
  $args