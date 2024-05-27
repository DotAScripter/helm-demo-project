PROJECT=pyapp
REPO_ROOT := $(shell git rev-parse --show-toplevel 2> /dev/null)

.PHONY: image
image: protogen
	docker build --build-arg PROJECT=$(PROJECT) . -t $(PROJECT):1.0

.PHONY: protogen
protogen:
	mkdir -p proto
	python3 -m grpc_tools.protoc -I$(REPO_ROOT)/interface --python_out=proto --grpc_python_out=proto status/status.proto
