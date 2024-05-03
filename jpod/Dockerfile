#Dockerfile for copying executable and running app
FROM alpine:3.19.1

RUN apk add openjdk17

RUN mkdir jpod

COPY ./jpod/target/jpod-1.0.jar /jpod

WORKDIR /jpod

ENTRYPOINT ["java", "-jar", "/jpod/jpod-1.0.jar"]