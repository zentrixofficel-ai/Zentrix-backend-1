FROM python:3.11-slim

WORKDIR /app

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
ENV ZENTRIX_HOST=0.0.0.0
ENV ZENTRIX_PORT=8080

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt || true

COPY . /app/backend

EXPOSE 8080

CMD ["python", "-m", "backend.server"]
