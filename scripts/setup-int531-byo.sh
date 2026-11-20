#!/usr/bin/env bash
set -Eeuo pipefail

fail() {
  printf 'Error: %s\n' "$*" >&2
  exit 1
}

readonly SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
readonly APP_DIR="$(cd -- "$SCRIPT_DIR/.." && pwd)"

RUN_HOME="${HOME}"
if [[ -n "${SUDO_USER:-}" ]]; then
  RUN_HOME="$(getent passwd "$SUDO_USER" | cut -d: -f6)"
fi
readonly LAB_DIR="${INT531_LAB_DIR:-$RUN_HOME/int531-slo-chaos-lab}"
readonly CRON_USER="${SUDO_USER:-$(id -un)}"

as_cron_user() {
  if [[ "$(id -un)" == "$CRON_USER" ]]; then
    "$@"
  else
    sudo -u "$CRON_USER" "$@"
  fi
}

install_daily_check_cron() {
  local current_crontab filtered_crontab
  as_cron_user mkdir -p "$LAB_DIR/logs"
  current_crontab="$(as_cron_user crontab -l 2>/dev/null || true)"
  filtered_crontab="$(printf '%s\n' "$current_crontab" | awk '
    $0 == "# BEGIN SSI-1 check-collecting" { in_block = 1; next }
    $0 == "# END SSI-1 check-collecting" { in_block = 0; next }
    in_block { next }
    index($0, "scripts/check-collecting.sh") && index($0, "check-collecting.log") { next }
    { print }
  ')"

  {
    printf '%s\n' "$filtered_crontab"
    printf '\n# BEGIN SSI-1 check-collecting\n'
    printf '0 9 * * * cd "%s" && /bin/bash scripts/check-collecting.sh >> "%s/logs/check-collecting.log" 2>&1\n' "$LAB_DIR" "$LAB_DIR"
    printf '# END SSI-1 check-collecting\n'
  } | as_cron_user crontab -
  printf 'Installed daily check-collecting cron job for %s at 09:00 server time.\n' "$CRON_USER"
}

ensure_cron_daemon() {
  if systemctl is-active --quiet cron || systemctl is-active --quiet crond; then
    return
  fi

  if systemctl list-unit-files --type=service --no-legend | awk '$1 == "cron.service" { found = 1 } END { exit !found }'; then
    sudo systemctl enable --now cron
    return
  fi
  if systemctl list-unit-files --type=service --no-legend | awk '$1 == "crond.service" { found = 1 } END { exit !found }'; then
    sudo systemctl enable --now crond
    return
  fi
  fail "Cron is not installed. Install the cron package, then run this script again."
}

for command in docker curl sudo crontab; do
  command -v "$command" >/dev/null 2>&1 || fail "Required command '$command' is missing."
done
docker compose version >/dev/null 2>&1 || fail "Docker Compose plugin is required."
docker info >/dev/null 2>&1 || fail "Cannot access the Docker daemon. Run this script with sudo or grant this user Docker access."
command -v systemctl >/dev/null 2>&1 || fail "This script requires systemd, as in the lab's system-service workflow."

[[ -d "$LAB_DIR" ]] || fail "Cannot find the INT531 lab checkout at $LAB_DIR. Set INT531_LAB_DIR to its path."
[[ -f "$LAB_DIR/scripts/healthcheck.sh" ]] || fail "Missing $LAB_DIR/scripts/healthcheck.sh."
[[ -f "$LAB_DIR/sizing/sizing.sh" ]] || fail "Missing $LAB_DIR/sizing/sizing.sh."
[[ -f "$LAB_DIR/scripts/install-service.sh" ]] || fail "Missing $LAB_DIR/scripts/install-service.sh."
[[ -f "$LAB_DIR/scripts/check-collecting.sh" ]] || fail "Missing $LAB_DIR/scripts/check-collecting.sh."
[[ -f "$APP_DIR/docker-compose.yml" ]] || fail "Cannot find docker-compose.yml in $APP_DIR."

printf 'Running INT531 preflight...\n'
cd "$LAB_DIR"
bash scripts/healthcheck.sh
bash sizing/sizing.sh --write

printf '\nStarting the SSI-1 services...\n'
cd "$APP_DIR"
docker compose up -d --build

published_port="$(docker compose port frontend 80 2>/dev/null || true)"
[[ -n "$published_port" ]] || fail "Could not read the published Nginx port from Docker Compose."
frontend_port="${published_port##*:}"
[[ "$frontend_port" =~ ^[0-9]+$ ]] || fail "Could not parse the Nginx port from '$published_port'."
target="http://127.0.0.1:${frontend_port}/ssi1/itb-mshop/v2/sale-items"

printf 'Waiting for the read-only API target %s ...\n' "$target"
ready=0
for attempt in {1..60}; do
  if curl --fail --silent --max-time 5 "$target" -o /dev/null; then
    ready=1
    break
  fi
  sleep 3
done
[[ "$ready" -eq 1 ]] || {
  docker compose ps >&2
  docker compose logs --tail=100 db_server backend frontend >&2
  fail "The app did not become ready."
}

printf '\nInstalling the BYO probe (one request per second)...\n'
cd "$LAB_DIR"
sudo bash "$LAB_DIR/scripts/install-service.sh" \
  --mode byo \
  --target "$target" \
  --interval 1

ensure_cron_daemon
install_daily_check_cron

printf 'Waiting for probe metrics and a Prometheus scrape...\n'
metrics_ready=0
for attempt in {1..30}; do
  if curl --fail --silent --max-time 3 http://127.0.0.1:9105/metrics -o /dev/null; then
    metrics_ready=1
    break
  fi
  sleep 2
done
[[ "$metrics_ready" -eq 1 ]] || fail "The probe metrics endpoint did not start on port 9105."
sleep 15

printf '\nChecking collection status...\n'
set +e
bash "$LAB_DIR/scripts/check-collecting.sh"
collecting_status=$?
set -e

case "$collecting_status" in
  0)
    printf '\nThe app and probe are running, and collection is healthy.\n'
    ;;
  1)
    printf '\nThe app and probe are installed; the lab check reported a warning. Review its output.\n' >&2
    ;;
  2)
    printf '\nThe app and probe are installed, but collection is not confirmed yet. Review the lab check output and Prometheus scrape target.\n' >&2
    ;;
  *)
    fail "check-collecting.sh exited with status $collecting_status."
    ;;
esac

printf 'App URL: http://<server-ip>:%s\n' "$frontend_port"
printf 'Probe metrics URL: http://<server-ip>:9105/metrics\n'
exit "$collecting_status"
