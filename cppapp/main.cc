#include <iostream>
#include <csignal>
#include <thread>
#include <condition_variable>
#include <grpcpp/grpcpp.h>
#include "proto/helloworld/helloworld.pb.h"
#include "proto/helloworld/helloworld.grpc.pb.h"

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
std::unique_ptr<Server> server;

void handleSignal(int sig) {
    std::cout << "Received signal: " << strsignal(sig) << std::endl;
    shutdownRequired = true;
    cv.notify_one();
}

void shutdownCheckingThread(void){
    std::unique_lock<std::mutex> lock(mutex);
    cv.wait(lock, [&](){ return shutdownRequired; });
    if (server != nullptr) {
        server->Shutdown();
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

std::string getEnvOrDefault(const char* name, const std::string& defaultValue) {
    const char* envValue = std::getenv(name);
    if (envValue == nullptr) {
        return defaultValue;
    }
    return std::string(envValue);
}

void runServer() {
    const std::string serverPort = getEnvOrDefault("SERVICE_PORT", "50051");
    const std::string serverAddress = getEnvOrDefault("SERVICE_ADDRESS", "127.0.0.1");
    const std::string serverAddressWithPort = serverAddress + ":" + serverPort;

    GreeterServiceImpl greeterService;

    ServerBuilder serverBuilder;
    serverBuilder.AddListeningPort(serverAddressWithPort, grpc::InsecureServerCredentials());
    serverBuilder.RegisterService(&greeterService);

    server = serverBuilder.BuildAndStart();
    std::cout << "Server listening on " << serverAddressWithPort << std::endl;

    server->Wait();
}

int main() {
    std::signal(SIGTERM, handleSignal);
    std::thread shutdownThread(shutdownCheckingThread);
    int exitCode = 0;
    try {
        runServer();
    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
        exitCode = 1;
    }
    shutdownThread.join();
    std::exit(exitCode);
}
