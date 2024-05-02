FROM mcr.microsoft.com/devcontainers/go:1-1.22-bookworm

# Install necessary build tools
RUN apt-get update && \
    apt-get install -y build-essential

# Install Boost dependencies
RUN apt-get install -y libboost-all-dev

RUN go install google.golang.org/protobuf/cmd/protoc-gen-go@v1.28
RUN go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@v1.2