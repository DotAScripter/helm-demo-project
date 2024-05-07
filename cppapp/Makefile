PROJECT=cppapp
REPO_ROOT := $(shell git rev-parse --show-toplevel 2> /dev/null)

GRPC_CPP_PLUGIN_PATH ?= `which grpc_cpp_plugin`
PROTOC ?= `which protoc`
PROTO_SRCS := $(REPO_ROOT)/interface/helloworld.proto
PROTO_GRPC_TARGET := proto/helloworld.grpc.pb.cc
PROTO_TARGET := proto/helloworld.pb.cc
PROTO_TARGET_DIR := proto

CXX=g++
CXXFLAGS=-std=c++11 -Wall

OBJ_DIR := out
SRC_DIRS := ./ ./proto
SRCS := $(foreach dir,$(SRC_DIRS),$(wildcard $(dir)*.cc))
OBJ_DIR := out
OBJS := $(patsubst %.cc,$(OBJ_DIR)/%.o,$(notdir $(SRCS)))
TARGET=$(PROJECT)

GRPC_INCLUDE=/usr/include
PROTOBUF_INCLUDE=/usr/include

LIBS = -lgrpc++ -lgrpc -lprotobuf -lpthread -ldl -lgpr -labsl_synchronization

.PHONY: image
image:
	mv $(TARGET) docker
	docker build --build-arg PROJECT=$(PROJECT) ./docker -t $(PROJECT):1.0

.PHONY: protogen
protogen: $(PROTO_GRPC_TARGET) $(PROTO_TARGET)

$(TARGET): $(OBJS) $(PROTO_GRPC_TARGET) $(PROTO_TARGET)
	$(CXX) $(CXXFLAGS) -o $@ $^ $(LIBS)

$(OBJ_DIR)/%.o: %.cc | $(OBJ_DIR)
	$(CXX) $(CXXFLAGS) -I$(GRPC_INCLUDE) -I$(PROTOBUF_INCLUDE) -c -o $@ $<

$(PROTO_GRPC_TARGET): $(PROTO_SRCS) | $(PROTO_TARGET_DIR)
	$(PROTOC) -I $(REPO_ROOT)/interface --grpc_out=proto --plugin=protoc-gen-grpc=$(GRPC_CPP_PLUGIN_PATH) $<

$(PROTO_TARGET): $(PROTO_SRCS) | $(PROTO_TARGET_DIR)
	$(PROTOC) -I $(REPO_ROOT)/interface --cpp_out=proto $<

$(OBJ_DIR):
	mkdir -p $(OBJ_DIR)

$(PROTO_TARGET_DIR):
	mkdir -p $(PROTO_TARGET_DIR)

.PHONY: clean
clean:
	rm -rf $(OBJ_DIR) $(TARGET) proto/ docker/$(PROJECT)
