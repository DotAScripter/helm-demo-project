# Helm Demo Project

## Deploy

1. kind create cluster --config=kind_config.yaml
2. make -C greetings build image
3. make -C cppapp cppapp image
4. kind load docker-image greetings:1.0 cppapp:1.0
5. helm dependency update charts/top
6. helm install test charts/top
7. Verify using curl http://127.0.0.1:30000/hello

## Update the top level helm chart (needs to be done after the charts have been updated)

1. helm dependency update charts/top

## Run unit-tests

1. make -C greetings test