FROM python:3.11-alpine3.18

WORKDIR /app

COPY ./app.py /app
COPY ./ /app

RUN pip install flask waitress grpcio grpcio-tools

CMD ["python", "app.py"]
