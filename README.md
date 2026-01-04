# Tulipmetric 
산업군 비교 서비스

# 개발 단계
배포 전 최종 통합 테스트 / 리팩터링 단계

## <a href="https://www.figma.com/board/ky0Sb60izZ4Ah3MlDtEusG/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98?node-id=0-1&t=WhjYw5i6j7hK7Kfi-1">아키텍처</a>
figma

## 사이트 주요 기능
- 코스피 시장에 등록된 모든 26개 산업군에 대한 성장률 과 최근 12개월간의 지수 변동을 이용해 시장 과열도를 비교할 수 있는 사이트임.
- 커뮤니티는 주식과 시장에 대해 자유롭게 토론과 소통을 할 수 있는 페이지임.
- 로그인을 하지 않아도 메인 페이지에서 산업군을 즐겨찾기가 가능하고 컴퓨터 껐다 켜도 로컬 스토리지를 활용해 즐겨찾기가 유지됨.
- 로그인을 하면 커뮤니티 사이트 게시글 게시, 댓글 등록등의 활동이 가능하며, 즐겨찾기한 산업군을 외부 컴퓨터 에서도 접근가능함.

## 환경 설정
1. `.env.example`을 `.env`로 복사해 DB, OAuth, JWT 설정을 채웁니다. 실제 `.env` 파일은 Git에 커밋되지 않도록 `.gitignore`에 추가되어 있습니다.
2. `src/main/resources/application-example.properties`를 `application.properties`로 복사한 뒤, 필요 시 `.env` 또는 환경 변수에서 값을 주입하도록 `${…}` 플레이스홀더를 유지합니다.
3. 민감 정보는 환경 변수나 `.env`/`.env.properties` 파일을 통해 주입할 수 있으며, `spring.config.import=optional:file:.env[.properties]` 설정으로 외부 파일 로딩을 허용합니다.
4. 이 레포지토리에서 노출된 이전 자격 증명(DB 비밀번호, Google/Naver OAuth 클라이언트, JWT 시크릿)은 모두 폐기/재발급하고, 관련 보안 이슈를 추적하세요.

### 개발 환경
- java 17
- springframework 4.0.1
- Spring Data JPA
- Spring Security
- Thymeleaf
