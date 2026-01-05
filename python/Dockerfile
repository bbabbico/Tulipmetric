FROM python:3.11-slim

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    TZ=Asia/Seoul

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY ["Sql_Insert_API.py", "주식별 업종 분류.xlsx", "./"]

ENV AUTO_INIT=true

CMD ["python", "/app/Sql_Insert_API.py"]
