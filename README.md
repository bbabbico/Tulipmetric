# Tulipmetric 
주식시장 산업군 비교 서비스

# 개발 단계
아키텍처 설계 / 스켈레톤 코드 작성 JAVA (완료) <br>
주식 데이터 도메인 코드 작성 JAVA (완료) <br>
MySql + JPA 코드 작성 / API 엔드포인트 컨트롤러 작성 JAVA (완료) <br>
주식 / 산업군 데이터 정보 수집 프로그램 작성 Python (완료) <br>
메인 페이지 프론트 엔드 작업 - GPT Codex (완료) <br>
회원가입 로그인 기능 구현 Spring Security (완료) <br>
커뮤니티 페이지 백엔드 구현 JAVA (완료) <br>
커뮤니티 페이지 프론트 엔드 작업 - GPT Codex (완료) <br>
기능 테스트 (완료) <br>
통합 테스트 (완료) <br>

1차 리팩터링 - 최종 배포를 위한 Nginx 도입. (완료) <br>
2차 리팩터링 - 레이어드 아키텍처 형식에 맞게 코드 수정 (완료) <br>
3차 리팩터링 Tulipmetric 로고 추가 (완료) <br>

(현재 진행중)
HTTPS 적용 + 도메인 관리 
서비스 확장 / 유지보수 기간

## <a href="https://www.figma.com/board/ky0Sb60izZ4Ah3MlDtEusG/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98?node-id=0-1&t=WhjYw5i6j7hK7Kfi-1">아키텍처 - figma</a>
/architecture 로 런타임 에서 아키텍처 소개 볼 수 있습니다. <br>
Architecture.md 파일에 아키텍처 소개가 있습니다.

## 환경 설정
1. `.env.example`을 `.env`로 복사해 DB, OAuth, JWT 설정을 채웁니다. 실제 `.env` 파일은 Git에 커밋되지 않도록 `.gitignore`에 추가되어 있습니다.
2. 민감 정보는 환경 변수나 `.env`/`.env.properties` 파일을 통해 주입할 수 있으며, `spring.config.import=optional:file:.env[.properties]` 설정으로 외부 파일 로딩을 허용합니다.

## 빌드
실제 주식시장 데이터를 이용하기 때문에, KOSIS API 키와 공공데이터 포탈 API 가 필요합니다. 

DATA_GO_KR_SERVICE_KEY=9k9a3/dWfMNnVqG9mRPMQQ28gKPieL/zP8QrxRrOr6gP71ukyqvL7voKvS6fYOooEJiA1/UjQ/SS0sg49nZEUA==
KOSIS_API_KEY=KOSIS_API_KEY=ZmVmMjhjMjMwYTBlZjcxODdlMWE4NGM0YjA5NjgxMWU=

이 API 주소를 사용함. 

최종빌드

`docker compose up -d --build`

## 사이트 주요 기능
- 코스피 시장에 등록된 모든 26개 산업군에 대한 성장률 과 최근 12개월간의 지수 변동을 이용해 시장 과열도를 비교할 수 있는 사이트임.
- 커뮤니티는 주식과 시장에 대해 자유롭게 토론과 소통을 할 수 있는 페이지임.
- 로그인을 하지 않아도 메인 페이지에서 산업군을 즐겨찾기가 가능하고 컴퓨터 껐다 켜도 로컬 스토리지를 활용해 즐겨찾기가 유지됨.
- 로그인을 하면 커뮤니티 사이트 게시글 게시, 댓글 등록등의 활동이 가능하며, 즐겨찾기한 산업군을 외부 컴퓨터 에서도 접근가능함.

### 개발 환경
- java 17
- springframework 4.0.1 / image - Gradle (Docker)
- Spring Data JPA
- Spring Security
- Thymeleaf
- MySql 8.0 (Docker)
- image - python:3.11-slim (Docker)
- image - nginx:1.25-alpine (Docker)

## GitHub Actions CI/CD 가이드 - 여기부터 AI 작성(테스트중)
현재 배포 방식(`docker compose up -d --build`)을 유지하면서 GitHub Actions로 자동화했습니다.

### 1) CI (`.github/workflows/ci.yml`)
- 트리거: `pull_request`, `push(main/develop)`
- 수행 내용
  - JDK 17 환경 구성
  - GitHub Secrets를 이용해 CI 런타임에 `.env` 생성
  - CI 전용 `application.properties` 생성 (OAuth/JWT/reCAPTCHA 포함)
  - MySQL 서비스 컨테이너 실행
  - `./gradlew test` 수행

> 민감정보 파일(`.env`, `application.properties`)은 저장소에 올리지 않습니다. CI 실행 중에만 파일을 생성하고 Job 종료 시 폐기됩니다.

### 2) CD (`.github/workflows/cd.yml`)
- 트리거: `CI` 워크플로가 `main` 브랜치에서 성공적으로 끝났을 때
- 수행 내용
  - 배포 서버 SSH 접속
  - 최신 `main` 반영 (`git pull`)
  - `docker compose up -d --build` 실행

### 3) GitHub Secrets 설정
저장소 `Settings > Secrets and variables > Actions`에 아래 값을 등록하세요.

#### CD 배포용 (필수)
- `DEPLOY_HOST`: 배포 서버 IP 또는 도메인
- `DEPLOY_USER`: SSH 사용자
- `DEPLOY_SSH_KEY`: 개인키(PEM 전체)
- `DEPLOY_PORT`: SSH 포트 (기본 22)
- `DEPLOY_PATH`: 서버 내 프로젝트 경로 (예: `/home/ubuntu/Tulipmetric`)

#### CI 애플리케이션 설정용 (권장)
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `NAVER_CLIENT_ID`
- `NAVER_CLIENT_SECRET`
- `JWT_SECRET`
- `RECAPTCHA_SITE_KEY`
- `RECAPTCHA_SECRET_KEY`

> 위 CI Secrets가 없으면 워크플로가 더미값으로 테스트를 수행합니다. 실제 OAuth 연동 검증이 필요하면 반드시 실제 키를 넣어 주세요.

### 4) 서버 측 준비
- 서버에는 Docker / Docker Compose가 설치되어 있어야 합니다.
- 최초 1회 서버에 프로젝트를 클론해 두세요.
- `.env`, `src/main/resources/application.properties`는 서버에 직접 배치해 두고 권한을 제한하세요.
