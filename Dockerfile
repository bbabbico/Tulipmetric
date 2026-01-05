# ====== 1단계: 빌드(Stage: builder) ======
FROM eclipse-temurin:17-jdk-alpine AS builder

# 작업 디렉토리
WORKDIR /build

# Gradle wrapper & 설정 파일들 복사
# (build.gradle vs build.gradle.kts 상황에 맞게 수정)
COPY gradlew settings.gradle* build.gradle* ./
COPY gradle gradle

# 의존성 미리 받아두기 (캐시 활용)
RUN ./gradlew dependencies --no-daemon || return 0

# 실제 소스 복사
COPY src src

# JAR 빌드 (스프링부트면 보통 bootJar)
RUN ./gradlew bootJar --no-daemon

# ====== 2단계: 런타임(Stage: runtime) ======
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

# 빌드 결과물 JAR 복사 (버전 이름 상관없이 *.jar 가져오기)
ARG JAR_FILE=build/libs/*.jar
COPY --from=builder /build/${JAR_FILE} spring_server.jar

# 컨테이너 시작 시 실행 명령
ENTRYPOINT ["java", "-jar", "/app/spring_server.jar"]