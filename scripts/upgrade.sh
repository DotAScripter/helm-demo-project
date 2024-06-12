#!/bin/bash

GREETINGS="greetings"
CPPOD="cppod"
JPOD="jpod"
PYPOD="pypod"

image_repository=""
image_digest=""
services=()

usage() {
    echo "Usage: $0 --services <service1,service2,...>"
    exit 1
}

# Parse options using getopt
PARSED_OPTIONS=$(getopt -o s: --long services: -- "$@")
if [ $? -ne 0 ]; then
    usage
fi

# Apply the parsed options
eval set -- "$PARSED_OPTIONS"

# Process the options
while true; do
    case "$1" in
        -s|--services)
            IFS=',' read -r -a services <<< "$2"
            shift 2
            ;;
        --)
            shift
            break
            ;;
        *)
            usage
            ;;
    esac
done

function log() {
    printf "### $1 ###\n"
}

function helm_upgrade(){
    log "Running helm upgrade charts/$1..."
    helm upgrade $1 charts/$1
    log "$1 Helm upgrade is done"
}

if [ ${#services[@]} -eq 0 ]; then
    usage
fi

echo "Services: ${services[*]}"

for service in "${services[@]}"; do
    echo "Processing service: $service"
    case $service in
    ${GREETINGS})
        log "Building ${GREETINGS} app and image..."
        make -C ${GREETINGS} build image
        log "Done building ${GREETINGS} app and image"

        log "Loading docker image into kind cluster (this might take some time)..."
        kind load docker-image ${GREETINGS}:1.0
        log "Done loading docker image into kind cluster"

        helm_upgrade ${GREETINGS}
        ;;
    ${CPPOD})
        log "Building ${CPPOD} app and image..."
        make -C cppapp protogen cppapp image
        log "Done building ${CPPOD} app and image"

        log "Loading docker image into kind cluster (this might take some time)..."
        kind load docker-image cppapp:1.0
        log "Done loading docker image into kind cluster"

        helm_upgrade ${CPPOD}
        ;;
    ${JPOD})
        log "Building ${JPOD} app and image..."
        make -C ${JPOD}/${JPOD} build image
        log "Done building ${JPOD} app and image"

        log "Loading docker image into kind cluster (this might take some time)..."
        kind load docker-image ${JPOD}:1.0
        log "Done loading docker image into kind cluster"

        helm_upgrade ${JPOD}
        ;;
    ${PYPOD})
        log "Building ${PYPOD} image..."
        make -C ${PYPOD} image
        log "Done building ${PYPOD} image"

        log "Loading docker image into kind cluster (this might take some time)..."
        kind load docker-image pyapp:1.0
        log "Done loading docker image into kind cluster"

        helm_upgrade ${PYPOD}
        ;;
    *)
        echo "Unknown service: $service"
        usage
        ;;
    esac
done

log "Upgrading Services: ${services[*]} done"
log "Test with: curl http://127.0.0.1:30000/hello/cppod"

exit 0
