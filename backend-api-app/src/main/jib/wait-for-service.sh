#!/usr/bin/env sh

echo "Start waiting for a service on the host: ${1:-localhost}."
while ! nc -z "${1:-localhost}" "${2:-3306}";
do
  echo "Waiting for: ${1:-localhost} to be ready.";
  sleep 5;
done;
echo "Service ${1:-localhost} is ready."