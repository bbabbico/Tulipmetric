# TLS Certificates (Let's Encrypt + Certbot)

이 디렉터리는 certbot의 `--config-dir`로 사용됩니다.

- 최초 발급 후 Nginx가 읽는 파일
  - `fullchain.pem`
  - `privkey.pem`
- certbot 원본 저장 경로
  - `live/<domain>/fullchain.pem`
  - `live/<domain>/privkey.pem`

## 최초 발급

1. `.env`에 `CERTBOT_DOMAIN`, `CERTBOT_EMAIL` 설정 (`CERTBOT_DOMAIN`은 공인 도메인만 가능, 로컬 IP 불가)
2. Nginx(80포트) 실행
3. 아래 명령으로 최초 인증서 발급

```bash
docker compose up -d nginx
docker compose --profile certbot-init run --rm certbot-init
```

## 자동 갱신

`certbot` 서비스가 12시간마다 `certbot renew`를 실행합니다.
갱신이 발생하면 `fullchain.pem`, `privkey.pem`에 동기화됩니다.

> 참고: Nginx 인증서 재로드(nginx -s reload)는 운영 환경에서 주기적으로 수행되도록 배치(cron/systemd timer 등)하거나 배포 파이프라인에 포함하는 것을 권장합니다.
