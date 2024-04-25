FROM alpine:3.19.1

RUN apk add libc6-compat

ARG PROJECT

COPY ./${PROJECT} /usr/local/bin

ENV ENTRYOINT_PATH="/usr/local/bin/${PROJECT}"

ENTRYPOINT $ENTRYOINT_PATH
