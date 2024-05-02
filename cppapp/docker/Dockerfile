FROM alpine:3.19.1

RUN apk add libc6-compat
RUN apk add --no-cache libstdc++
RUN apk add --no-cache libgcc

# Install Boost dependencies
RUN apk add boost-dev

ARG PROJECT

COPY ./${PROJECT} /usr/local/bin

ENV ENTRYOINT_PATH="/usr/local/bin/${PROJECT}"

ENTRYPOINT $ENTRYOINT_PATH
