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

Set `FRONTEND_PORT` and `BACKEND_PORT` in `.env` to change the host ports. Set
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

## Deploy on a Linux server

The deployment script clones or fast-forwards this repository, creates a
private `.env` with fresh database and JWT secrets on first run, starts the
Compose services, and waits for the frontend and a read-only API request to
respond. It does not install monitoring services.

Requirements: Git, Docker Engine with the Compose plugin, `curl`, `openssl`,
and permission to access Docker and write to `/opt/ssi-1`.

```sh
sudo bash scripts/deploy-server.sh
```

The default Git URL uses SSH. Override it and the install directory when needed:

```sh
sudo SSI_REPO_URL=https://github.com/Pornput0012/SSI-1.git \
  SSI_APP_DIR=/srv/ssi-1 bash scripts/deploy-server.sh
```

On the first run, the script creates `/opt/ssi-1/.env`. Edit `APP_HOST_URL` and
`JWT_ISSUER` there to match the server's public frontend URL, then rerun the
script. It preserves an existing `.env` on subsequent deployments. The VM's
firewall must allow inbound traffic on the configured `FRONTEND_PORT` (5173 by
default).
