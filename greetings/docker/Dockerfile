FROM alpine:3.19.1

RUN apk add libc6-compat

ARG PROJECT

COPY ./${PROJECT} /usr/local/bin/greetings

ENTRYPOINT ["/usr/local/bin/greetings"]
