#!/usr/bin/env bash
set -Eeuo pipefail

readonly DEFAULT_REPO_URL="git@github.com:Pornput0012/SSI-1.git"
readonly DEFAULT_BRANCH="main"
readonly APP_DIR="${SSI_APP_DIR:-/opt/ssi-1}"
readonly REPO_URL="${SSI_REPO_URL:-$DEFAULT_REPO_URL}"
readonly BRANCH="${SSI_BRANCH:-$DEFAULT_BRANCH}"

fail() {
  printf 'Error: %s\n' "$*" >&2
  exit 1
}

require_command() {
  command -v "$1" >/dev/null 2>&1 || fail "Required command '$1' is not installed."
}

require_command git
require_command docker
require_command curl
require_command openssl

docker compose version >/dev/null 2>&1 || fail "Docker Compose plugin is required."
docker info >/dev/null 2>&1 || fail "Docker is not running or this user cannot access the Docker daemon."

if [[ -e "$APP_DIR" ]]; then
  [[ -d "$APP_DIR/.git" ]] || fail "$APP_DIR exists but is not a Git checkout. Set SSI_APP_DIR to another path."
  current_branch="$(git -C "$APP_DIR" branch --show-current)"
  [[ "$current_branch" == "$BRANCH" ]] || fail "$APP_DIR is on branch '$current_branch'; expected '$BRANCH'."
  [[ -z "$(git -C "$APP_DIR" status --porcelain --untracked-files=no)" ]] || \
    fail "$APP_DIR has modified tracked files. Commit or revert them before deploying."
  git -C "$APP_DIR" pull --ff-only origin "$BRANCH"
else
  mkdir -p "$(dirname "$APP_DIR")" || fail "Cannot create the parent directory for $APP_DIR. Run with appropriate permissions or set SSI_APP_DIR."
  git clone --branch "$BRANCH" --single-branch "$REPO_URL" "$APP_DIR"
fi

[[ -f "$APP_DIR/docker-compose.yml" ]] || fail "No docker-compose.yml found in $APP_DIR."
[[ -f "$APP_DIR/.env.example" ]] || fail "No .env.example found in $APP_DIR."

env_file="$APP_DIR/.env"
if [[ ! -f "$env_file" ]]; then
  cp "$APP_DIR/.env.example" "$env_file"
  set_env_value() {
    local key="$1"
    local value="$2"
    if grep -q "^${key}=" "$env_file"; then
      sed -i "s|^${key}=.*|${key}=${value}|" "$env_file"
    else
      printf '%s=%s\n' "$key" "$value" >> "$env_file"
    fi
  }

  set_env_value MYSQL_ROOT_PASSWORD "$(openssl rand -hex 24)"
  set_env_value MYSQL_PASSWORD "$(openssl rand -hex 24)"
  set_env_value JWT_SECRET_VERIFY_EMAIL "$(openssl rand -hex 32)"
  set_env_value JWT_SECRET_RESET_PASSWORD "$(openssl rand -hex 32)"
  set_env_value JWT_SECRET_ACCESS "$(openssl rand -hex 32)"
  set_env_value JWT_SECRET_REFRESH "$(openssl rand -hex 32)"
  chmod 600 "$env_file"
  printf 'Created %s with generated database passwords and JWT secrets.\n' "$env_file"
else
  chmod 600 "$env_file"
  printf 'Using existing %s; current settings and secrets are preserved.\n' "$env_file"
fi

cd "$APP_DIR"
docker compose up -d --build

published_port="$(docker compose port frontend 80 2>/dev/null || true)"
[[ -n "$published_port" ]] || fail "Frontend container started, but its published port could not be read."
frontend_port="${published_port##*:}"
[[ "$frontend_port" =~ ^[0-9]+$ ]] || fail "Could not parse the frontend port from '$published_port'."
base_url="http://127.0.0.1:${frontend_port}"

printf 'Waiting for the frontend and read-only API endpoint at %s ...\n' "$base_url"
for attempt in {1..60}; do
  if curl --fail --silent --show-error --max-time 5 "$base_url/" -o /dev/null 2>/dev/null && \
    curl --fail --silent --show-error --max-time 5 \
      "$base_url/ssi1/itb-mshop/v2/sale-items" -o /dev/null 2>/dev/null; then
    printf '\nSSI-1 is running. Open http://<server-ip>:%s\n' "$frontend_port"
    docker compose ps
    exit 0
  fi
  sleep 3
done

printf 'The service did not become ready. Recent container logs:\n' >&2
docker compose ps >&2
docker compose logs --tail=100 db_server backend frontend >&2
exit 1
