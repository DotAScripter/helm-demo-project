# helm-demo-project
kind create cluster --config=kind_config.yaml
make build
make image
kind load docker-image greetings:1.0
helm install test charts/greetings/
curl http://127.0.0.1:30000/

# Update the top level helm chart (needs to be done after the charts have been updated)
helm dependency update charts/top
helm install test charts/top