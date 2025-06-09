#!/bin/bash

set -e

echo "JAR assembly"

cd "$(dirname "$0")"

cd auth_server
./mvnw clean package -Dmaven.test.failure.ignore=true
cd ..

cd resource_server
./mvnw clean package -Dmaven.test.failure.ignore=true
cd ..

echo "Starting Docker Compose"
docker compose -f compose.yaml up -d