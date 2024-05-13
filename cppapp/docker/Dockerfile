FROM ubuntu:24.04

RUN apt-get update && apt install -y libprotobuf-dev libgrpc++-dev

ARG PROJECT

COPY ./${PROJECT} /usr/local/bin

ENV ENTRYOINT_PATH="/usr/local/bin/${PROJECT}"

ENTRYPOINT $ENTRYOINT_PATH
