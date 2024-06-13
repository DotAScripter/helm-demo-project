FROM ubuntu:24.04

RUN apt-get update && apt install -y libprotobuf-dev libgrpc++-dev
RUN apt-get clean -y && rm -rf /var/lib/apt/lists/*

ARG PROJECT

COPY ./${PROJECT} /usr/local/bin/cppapp

ENTRYPOINT ["/usr/local/bin/cppapp"]
