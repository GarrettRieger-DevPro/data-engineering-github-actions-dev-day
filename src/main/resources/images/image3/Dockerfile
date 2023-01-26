FROM python:3.10

WORKDIR /app

COPY . .

RUN python -m pip install --no-cache-dir --upgrade pip && \
    python -m pip install --no-cache-dir -r requirements.txt

CMD ["python", "main.py"]

