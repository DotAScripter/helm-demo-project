PROJECT=pyapp
REPO_ROOT := $(shell git rev-parse --show-toplevel 2> /dev/null)

.PHONY: image
image: protogen
	docker build --build-arg PROJECT=$(PROJECT) . -t $(PROJECT):1.0

.PHONY: docker-run
docker-run: image
	docker run --rm $(PROJECT):1.0

.PHONY: protogen
protogen:
	python3 -m grpc_tools.protoc -I$(REPO_ROOT)/ --python_out=. --grpc_python_out=. interface/status/status.proto
	python3 -m grpc_tools.protoc -I$(REPO_ROOT)/ --python_out=. --grpc_python_out=. interface/helloworld/helloworld.proto

.PHONY: clean
clean:
	rm -rf interface
