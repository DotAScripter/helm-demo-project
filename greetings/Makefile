PROJECT=greetings

GO := GO111MODULE=on go

TEST_DIRS := `$(GO) list ./...`

GOBUILD := $(GO) build -o docker/$(PROJECT)
GOTEST := $(GO) test $(TEST_DIRS) --failfast

.PHONY: build
build:
	$(GOBUILD)

.PHONY: image
image:
	docker build --build-arg PROJECT=$(PROJECT) ./docker -t $(PROJECT):1.0

.PHONY: test
test:
	$(GOTEST)
