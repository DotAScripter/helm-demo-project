#!/bin/bash

function log() {
    printf "### $1 ###\n"
}

function helm_install(){
    log "Running helm install charts/$1..."
    helm install $1 charts/$1
    log "$1 Helm installation is ready!"
}

log "Building Go app and image..."
make -C greetings build image
log "Done building Go app and image"

log "Building Cpp app and image..."
make -C cppapp protogen cppapp image
log "Done building Cpp app and image"

log "Building Java app and image..."
make -C jpod/jpod build image
log "Done building Java app and image"

log "Building Pypod image..."
make -C pypod image
log "Done building Pypod image"

log "Creating kind kubernetes cluster..."
kind create cluster --config=kind_config.yaml
log "Done creating kind kubernetes cluster"

log "Loading docker images into kind cluster (this might take some time)..."
kind load docker-image greetings:1.0 cppapp:1.0 jpod:1.0 pyapp:1.0
log "Done loading docker images into kind cluster"

helm_install greetings
helm_install cppod
helm_install jpod
helm_install pypod
helm_install redis

log "Test with: curl http://127.0.0.1:30000/hello/cppod"

exit 0
