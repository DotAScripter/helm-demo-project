PROJECT=greetings
REPO_ROOT := $(shell git rev-parse --show-toplevel 2> /dev/null)

GO := GO111MODULE=on go

TEST_DIRS := `$(GO) list ./...`

GOBUILD := $(GO) build -o docker/$(PROJECT)
GOTEST := $(GO) test $(TEST_DIRS) --failfast -race

.PHONY: build
build: protogen
	$(GOBUILD)

.PHONY: image
image:
	docker build --build-arg PROJECT=$(PROJECT) ./docker -t $(PROJECT):1.0

.PHONY: protogen
protogen:
	mkdir -p proto
	protoc -I $(REPO_ROOT)/interface --go_out=proto --go_opt=paths=source_relative --go-grpc_out=proto --go-grpc_opt=paths=source_relative \
    helloworld.proto

.PHONY: test
test:
	$(GOTEST)

.PHONY: clean
clean:
	rm -rf proto/ docker/$(PROJECT)
