#!/bin/bash

function log() {
    printf "### $1 ###\n"
}

function helm_uninstall(){
    log "Deleting helm installation $1..."
    helm delete $1
    log "$1 Helm installation deleted"
}


helm_uninstall greetings
helm_uninstall cppod
helm_uninstall jpod
helm_uninstall pypod
helm_uninstall redis

log "Deleting kind kubernetes cluster..."
kind delete cluster
log "Kind kubernetes cluster deleted"
