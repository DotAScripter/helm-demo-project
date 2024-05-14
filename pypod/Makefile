PROJECT=pyapp
REPO_ROOT := $(shell git rev-parse --show-toplevel 2> /dev/null)

.PHONY: image
image:
	docker build --build-arg PROJECT=$(PROJECT) . -t $(PROJECT):1.0
