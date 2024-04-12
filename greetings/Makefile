PROJECT=greetings

GO := GO111MODULE=on go

GOBUILD := $(GO) build -o docker/$(PROJECT)

.PHONY: build
build:
	$(GOBUILD)

.PHONY: image
image:
	docker build --build-arg PROJECT=$(PROJECT) ./docker -t $(PROJECT):1.1