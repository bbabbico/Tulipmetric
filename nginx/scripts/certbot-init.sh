#!/bin/sh
set -eu

DOMAIN="${CERTBOT_DOMAIN:?CERTBOT_DOMAIN is required}"
EMAIL="${CERTBOT_EMAIL:?CERTBOT_EMAIL is required}"

mkdir -p /etc/nginx/certs /var/www/certbot

certbot certonly \
  --webroot -w /var/www/certbot \
  --email "$EMAIL" \
  -d "$DOMAIN" \
  --rsa-key-size 4096 \
  --agree-tos \
  --non-interactive \
  --config-dir /etc/nginx/certs \
  --work-dir /var/lib/letsencrypt \
  --logs-dir /var/log/letsencrypt

cp "/etc/nginx/certs/live/$DOMAIN/fullchain.pem" /etc/nginx/certs/fullchain.pem
cp "/etc/nginx/certs/live/$DOMAIN/privkey.pem" /etc/nginx/certs/privkey.pem

echo "[certbot-init] Initial certificate issued for $DOMAIN"
