# 공식 MySQL 8.0 이미지 기반
FROM mysql:8.0

# 기본 설정 TODO : 빌드 하려면 수정 필요
ENV MYSQL_ROOT_PASSWORD=. \
    MYSQL_DATABASE=. \
    MYSQL_USER=. \
    MYSQL_PASSWORD=. \
    TZ=Asia/Seoul

# 컨테이너 시작 시 자동 실행할 초기 스크립트가 있다면 여기에 복사
# (예: 테이블 생성, 기본 데이터 insert 등)
# init.sql 파일은 같은 폴더에 두면 됨
# 없으면 이 줄은 없어도 됨
COPY ./init.sql /docker-entrypoint-initdb.d/

# MySQL 포트
EXPOSE 3306