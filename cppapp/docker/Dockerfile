FROM ubuntu:24.04

RUN apt-get update && \
    apt-get install -y build-essential

RUN apt install -y libprotobuf-dev libgrpc-dev libgrpc++-dev
RUN apt install -y autoconf libtool pkg-config

RUN apt-get install -y libboost-all-dev

ARG PROJECT

COPY ./${PROJECT} /usr/local/bin

ENV ENTRYOINT_PATH="/usr/local/bin/${PROJECT}"

ENTRYPOINT $ENTRYOINT_PATH
