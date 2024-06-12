FROM mcr.microsoft.com/vscode/devcontainers/python:3.8

# Install C++ dependencies
RUN apt-get update && \
    apt-get install -y build-essential \
    protobuf-compiler \
    libprotobuf-dev \
    protobuf-compiler-grpc \
    libgrpc-dev \
    libgrpc++-dev \
    autoconf libtool \
    pkg-config \
    libboost-all-dev

# Install Python dependencies
RUN pip3 install grpcio grpcio-tools

# Install Maven and JDK
RUN apt-get install -y maven \
    openjdk-17-jdk

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$PATH:$JAVA_HOME/bin:/usr/share/maven/bin

# Install Go and necessary Go packages
RUN wget https://go.dev/dl/go1.22.4.linux-amd64.tar.gz
RUN rm -rf /usr/local/go && tar -C /usr/local -xzf go1.22.4.linux-amd64.tar.gz
RUN rm go1.22.4.linux-amd64.tar.gz
ENV PATH=$PATH:/usr/local/go/bin
ENV GOPATH=$HOME/go
ENV PATH=$PATH:$GOPATH/bin
RUN go install google.golang.org/protobuf/cmd/protoc-gen-go@v1.28
RUN go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@v1.2
RUN go install sigs.k8s.io/kind@v0.23.0

RUN  apt-get clean -y && rm -rf /var/lib/apt/lists/*
