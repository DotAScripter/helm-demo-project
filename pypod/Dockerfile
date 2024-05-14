FROM python:3.11-alpine3.18

WORKDIR /app

COPY ./app.py /app

RUN pip install flask waitress

CMD ["python", "app.py"]
