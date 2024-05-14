#!/bin/bash

function log() {
    printf "### $1 ###\n"
}

log "Deleting helm installation..."
helm delete test
log "Helm installation deleted"

log "Deleting kind kubernetes cluster..."
kind delete cluster
log "Kind kubernetes cluster deleted"
