# Helm Demo Project

## Install

### Manually

1. kind create cluster --config=kind_config.yaml
2. make -C greetings build image
3. make -C cppapp cppapp image
4. kind load docker-image greetings:1.0 cppapp:1.0
5. helm dependency update charts/top
6. helm install test charts/top
7. Verify using curl http://127.0.0.1:30000/hello

### Using script

1. Run [install.sh](./install.sh) to build images, create a kind cluster and install the helm chart
2. Run [upgrade.sh](./upgrade.sh) to re-build images and upgrade the helm chart (note that rollback will not work as expected since the last loaded image with tag xxx:1.0 will be used to start the pod after rollback)
3. Run [uninstall.sh](./uninstall.sh) to delete the helm installation and the kind cluster

## Update the top level helm chart (needs to be done after the charts have been updated)

1. helm dependency update charts/top

## Run unit-tests

1. make -C greetings test
