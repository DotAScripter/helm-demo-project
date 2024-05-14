#!/bin/bash

function log() {
    printf "### $1 ###\n"
}

log "Building Go app and image..."
make -C greetings build image
log "Done building Go app and image"

log "Building Cpp app and image..."
make -C cppapp cppapp image
log "Done building Cpp app and image"

log "Running helm dependency update..."
helm dependency update charts/top
log "Done running helm dependency update"

log "Loading docker images into kind cluster (this might take some time)..."
kind load docker-image greetings:1.0 cppapp:1.0
log "Done loading docker images into kind cluster"

log "Running helm upgrade..."
helm upgrade test charts/top
log "Helm upgrade is done"
log "Test with: curl http://127.0.0.1:30000/hello"
