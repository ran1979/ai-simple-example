#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

SPRING_PROFILES_ACTIVE=ollama ./mvnw spring-boot:run
