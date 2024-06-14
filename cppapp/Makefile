PROJECT=cppapp
REPO_ROOT := $(shell git rev-parse --show-toplevel 2> /dev/null)

GRPC_CPP_PLUGIN_PATH ?= `which grpc_cpp_plugin`
PROTOC ?= `which protoc`
HELLO_PROTO_SRCS := $(REPO_ROOT)/interface/helloworld/helloworld.proto
HELLO_PROTO_GRPC_TARGET := proto/helloworld/helloworld.grpc.pb.cc
HELLO_PROTO_TARGET := proto/helloworld/helloworld.pb.cc
STATUS_PROTO_SRCS := $(REPO_ROOT)/interface/status/status.proto
STATUS_PROTO_GRPC_TARGET := proto/status/status.grpc.pb.cc
STATUS_PROTO_TARGET := proto/status/status.pb.cc
PROTO_TARGET_DIR := proto

CXX=g++
CXXFLAGS=-std=c++11 -Wall

OBJ_DIR := out
SRC_DIRS := ./ ./proto/helloworld ./proto/status ./proto
SRCS := $(foreach dir,$(SRC_DIRS),$(wildcard $(dir)*.cc))
OBJ_DIR := out
OBJS := $(patsubst %.cc,$(OBJ_DIR)/%.o,$(notdir $(SRCS)))
TARGET=$(PROJECT)

GRPC_INCLUDE=/usr/include
PROTOBUF_INCLUDE=/usr/include

LIBS = -lgrpc++ -lgrpc -lprotobuf -lpthread -ldl -lgpr -labsl_synchronization

.PHONY: image
image:
	cp $(TARGET) docker
	docker build --build-arg PROJECT=$(PROJECT) ./docker -t $(PROJECT):1.0

.PHONY: protogen
protogen: $(HELLO_PROTO_GRPC_TARGET) $(HELLO_PROTO_TARGET) $(STATUS_PROTO_GRPC_TARGET) $(STATUS_PROTO_TARGET)

$(TARGET): $(OBJS) $(HELLO_PROTO_GRPC_TARGET) $(HELLO_PROTO_TARGET) $(STATUS_PROTO_GRPC_TARGET) $(STATUS_PROTO_TARGET)
	$(CXX) $(CXXFLAGS) -o $@ $^ $(LIBS) -I$(PROTO_TARGET_DIR)

$(OBJ_DIR)/%.o: %.cc | $(OBJ_DIR) protogen
	$(CXX) $(CXXFLAGS) -I$(GRPC_INCLUDE) -I$(PROTOBUF_INCLUDE) -I$(PROTO_TARGET_DIR) -c -o $@ $<

$(HELLO_PROTO_GRPC_TARGET): $(HELLO_PROTO_SRCS) | $(PROTO_TARGET_DIR)
	$(PROTOC) -I $(REPO_ROOT)/interface --grpc_out=proto --plugin=protoc-gen-grpc=$(GRPC_CPP_PLUGIN_PATH) $<

$(HELLO_PROTO_TARGET): $(HELLO_PROTO_SRCS) | $(PROTO_TARGET_DIR)
	$(PROTOC) -I $(REPO_ROOT)/interface --cpp_out=proto $<

$(STATUS_PROTO_GRPC_TARGET): $(STATUS_PROTO_SRCS) | $(PROTO_TARGET_DIR)
	$(PROTOC) -I $(REPO_ROOT)/interface --grpc_out=proto --plugin=protoc-gen-grpc=$(GRPC_CPP_PLUGIN_PATH) $<

$(STATUS_PROTO_TARGET): $(STATUS_PROTO_SRCS) | $(PROTO_TARGET_DIR)
	$(PROTOC) -I $(REPO_ROOT)/interface --cpp_out=proto $<

$(OBJ_DIR):
	mkdir -p $(OBJ_DIR)

$(PROTO_TARGET_DIR):
	mkdir -p $(PROTO_TARGET_DIR)

.PHONY: clean
clean:
	rm -rf $(OBJ_DIR) $(TARGET) proto/ docker/$(PROJECT)
