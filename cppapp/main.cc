#include <iostream>
#include <csignal>
#include <thread>
#include <condition_variable>
#include <grpcpp/grpcpp.h>
#include "proto/helloworld/helloworld.pb.h"
#include "proto/helloworld/helloworld.grpc.pb.h"
#include "proto/status/status.pb.h"
#include "proto/status/status.grpc.pb.h"

using grpc::Server;
using grpc::ServerBuilder;
using grpc::ServerContext;
using grpc::Status;
using helloworld::Greeter;
using helloworld::HelloRequest;
using helloworld::HelloReply;

std::mutex mutex;
bool shutdownRequired = false;
std::condition_variable cv;
std::unique_ptr<Server> greeterServer;
std::unique_ptr<Server> statusServer;

std::string getEnvOrDefault(const char* name, const std::string& defaultValue) {
    const char* envValue = std::getenv(name);
    if (envValue == nullptr) {
        return defaultValue;
    }
    return std::string(envValue);
}

void handleSignal(int sig) {
    std::cout << "Received signal: " << strsignal(sig) << std::endl;
    shutdownRequired = true;
    cv.notify_one();
}

void shutdownCheckingThread(void){
    std::unique_lock<std::mutex> lock(mutex);
    cv.wait(lock, [&](){ return shutdownRequired; });
    if (greeterServer != nullptr) {
        greeterServer->Shutdown();
    }
    if (statusServer != nullptr) {
        statusServer->Shutdown();
    }
}

class GreeterServiceImpl final : public Greeter::Service {
    Status SayHello(ServerContext* context, const HelloRequest* request,
                    HelloReply* reply) override {
        std::cout << "GreeterServiceImpl handling HelloRequest: " << request->DebugString() << std::endl;
        std::string message = "Hello, " + request->name();
        reply->set_message(message);
        return Status::OK;
    }
};

class StatusServiceImpl final : public status::Status::Service {
  grpc::Status CheckStatus(ServerContext* context, const status::StatusCheckRequest* request, status::StatusCheckResponse* response) override {
     std::cout << "StatusServiceImpl handling StatusCheckRequest: " << request->DebugString() << std::endl;
    return grpc::Status::OK;
  }
};

void runStatusServer() {
    const std::string serverPort = getEnvOrDefault("STATUS_SERVICE_PORT", "50052");
    const std::string serverAddress = getEnvOrDefault("STATUS_SERVICE_ADDRESS", "127.0.0.1");
    const std::string serverAddressWithPort = serverAddress + ":" + serverPort;

    StatusServiceImpl statusService;

    ServerBuilder serverBuilder;
    serverBuilder.AddListeningPort(serverAddressWithPort, grpc::InsecureServerCredentials());
    serverBuilder.RegisterService(&statusService);

    statusServer = serverBuilder.BuildAndStart();
    std::cout << "Server listening on " << serverAddressWithPort << std::endl;

    statusServer->Wait();
}

void runGreeterServer() {
    const std::string serverPort = getEnvOrDefault("GREETER_SERVICE_PORT", "50051");
    const std::string serverAddress = getEnvOrDefault("GREETER_SERVICE_ADDRESS", "127.0.0.1");
    const std::string serverAddressWithPort = serverAddress + ":" + serverPort;

    GreeterServiceImpl greeterService;

    ServerBuilder serverBuilder;
    serverBuilder.AddListeningPort(serverAddressWithPort, grpc::InsecureServerCredentials());
    serverBuilder.RegisterService(&greeterService);

    greeterServer = serverBuilder.BuildAndStart();
    std::cout << "Server listening on " << serverAddressWithPort << std::endl;

    greeterServer->Wait();
}

int main() {
    std::signal(SIGTERM, handleSignal);
    std::thread greeterServerThread(runGreeterServer);
    std::thread statusServerThread(runStatusServer);
    std::thread shutdownThread(shutdownCheckingThread);
    int exitCode = 0;
    try {
        greeterServerThread.join();
        statusServerThread.join();
    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
        exitCode = 1;
    }
    shutdownThread.join();
    std::exit(exitCode);
}
