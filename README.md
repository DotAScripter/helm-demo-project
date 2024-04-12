# helm-demo-project
kind create cluster --config=kind_config.yaml
make build
make image
kind load docker-image greetings:1.0
helm install test charts/greetings/
curl http://127.0.0.1:30000/