#!/bin/bash

set -e

echo "JAR assembly"

cd "$(dirname "$0")"

cd auth_server
./mvnw clean package -DskipTests
cd ..

cd resource_server
./mvnw clean package -DskipTests
cd ..

echo "Starting Docker Compose"
docker compose -f compose.yaml up -d