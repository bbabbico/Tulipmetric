#!/bin/sh
set -eu

DOMAIN="${CERTBOT_DOMAIN:?CERTBOT_DOMAIN is required}"

mkdir -p /etc/nginx/certs /var/www/certbot

while :; do
  certbot renew \
    --webroot -w /var/www/certbot \
    --quiet \
    --config-dir /etc/nginx/certs \
    --work-dir /var/lib/letsencrypt \
    --logs-dir /var/log/letsencrypt

  if [ -f "/etc/nginx/certs/live/$DOMAIN/fullchain.pem" ] && [ -f "/etc/nginx/certs/live/$DOMAIN/privkey.pem" ]; then
    cp "/etc/nginx/certs/live/$DOMAIN/fullchain.pem" /etc/nginx/certs/fullchain.pem
    cp "/etc/nginx/certs/live/$DOMAIN/privkey.pem" /etc/nginx/certs/privkey.pem
    echo "[certbot-renew] Synced renewed certificate for $DOMAIN"
  fi

  sleep 12h
 done
