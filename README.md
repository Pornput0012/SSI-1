# SSI-1

## Run with Docker Compose

Install Docker with the Compose plugin, then run:

```sh
cp .env.example .env
docker compose up --build
```

Open <http://localhost:5173>. Nginx serves the frontend and proxies `/ssi1/...`
requests to the backend. MySQL data and uploaded images are stored in named
Docker volumes.

Set `FRONTEND_PORT` in `.env` to change the frontend host port. The backend is
reachable through Nginx and is not published directly on the host. Set
`APP_HOST_URL` and `JWT_ISSUER` to the public frontend URL when deploying on a
server. Set `APP_CORS_ALLOWED_ORIGINS` if browser clients will call the backend
from other origins.

Email verification and password reset require a reachable SMTP server. Set
`MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, and the SMTP flags in
`.env` to enable it. Replace all development database passwords and JWT secrets
before exposing the deployment to other users.

The schema bootstrap in `db/init.sql` uses the database name `INT221`; keep the
MySQL database name aligned with it. The bootstrap script runs only when the
MySQL data volume is first created.

## Start the app and install the INT531 BYO probe

For the INT531 lab's section 3.3 workflow, run `scripts/setup-int531-byo.sh`
from this checkout. By default it looks for the lab checkout at
`~/int531-slo-chaos-lab`; set `INT531_LAB_DIR` if it is elsewhere. It runs the
lab preflight and sizing scripts, starts the app, then installs the BYO probe
against the read-only sale-item API through Nginx. The probe interval is one
second, as in the lab instructions. It also adds an idempotent 09:00 daily
Cron check for `check-collecting.sh`, logging to
`$INT531_LAB_DIR/logs/check-collecting.log`.

```sh
INT531_LAB_DIR="$HOME/int531-slo-chaos-lab" bash scripts/setup-int531-byo.sh
```

Run it as a user who can access the Docker daemon; the script uses `sudo` only
for the lab's system-service installer.

Nginx publishes port 5173 by default. The backend is internal to the Compose
network, so it does not conflict with cAdvisor on host port 8080. The probe
publishes metrics on port 9105. Allow inbound traffic on the frontend port if
the app should be reachable outside the VM.
