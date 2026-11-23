#!/usr/bin/env bash
set -Eeuo pipefail

# Determine repository root directory
readonly SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
readonly APP_DIR="$(cd -- "$SCRIPT_DIR/.." && pwd)"

echo "=========================================="
echo "  Deploying SSI-1 (Branch: int531)       "
echo "=========================================="
echo "Directory: $APP_DIR"
cd "$APP_DIR"

echo ""
echo "--> 1. Pulling latest changes from git..."
git pull origin int531

echo ""
echo "--> 2. Building and starting Docker containers..."
docker compose up -d --build

echo ""
echo "--> 3. Checking container status..."
docker compose ps

echo ""
echo "=========================================="
echo "  Deploy completed successfully!         "
echo "=========================================="
