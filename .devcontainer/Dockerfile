FROM mcr.microsoft.com/devcontainers/go:1-1.22-bookworm

# Install necessary build tools
RUN apt-get update && \
    apt-get install -y build-essential

#libprotoc-dev libc-ares-dev grpc
RUN apt install -y protobuf-compiler libprotobuf-dev protobuf-compiler-grpc libgrpc-dev libgrpc++-dev
RUN apt install -y autoconf libtool pkg-config

# Install Boost dependencies
RUN apt-get install -y libboost-all-dev

RUN apt-get install -y maven

RUN apt-get install -y openjdk-17-jdk

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$PATH:$JAVA_HOME/bin:/usr/share/maven/bin

RUN go install google.golang.org/protobuf/cmd/protoc-gen-go@v1.28
RUN go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@v1.2
