# 공식 MySQL 8.0 이미지 기반
FROM mysql:8.0

# 기본 설정 (원하면 값 바꿔도 됨)
ENV MYSQL_ROOT_PASSWORD=ehgus2003 \
    MYSQL_DATABASE=tulipmetric \
    MYSQL_USER=user \
    MYSQL_PASSWORD=(ehgus2003) \
    TZ=Asia/Seoul

# 컨테이너 시작 시 자동 실행할 초기 스크립트가 있다면 여기에 복사
# (예: 테이블 생성, 기본 데이터 insert 등)
# init.sql 파일은 같은 폴더에 두면 됨
# 없으면 이 줄은 없어도 됨
COPY ./init.sql /docker-entrypoint-initdb.d/

# MySQL 포트
EXPOSE 3306