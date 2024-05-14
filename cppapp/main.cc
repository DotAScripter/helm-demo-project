#include <boost/beast/core.hpp>
#include <boost/beast/http.hpp>
#include <boost/asio.hpp>
#include <iostream>
#include <grpcpp/grpcpp.h>
#include "proto/helloworld.pb.h"
#include "proto/helloworld.grpc.pb.h"

namespace beast = boost::beast;
namespace http = beast::http;
namespace net = boost::asio;

using tcp = net::ip::tcp;
using grpc::Server;
using grpc::ServerBuilder;
using grpc::ServerContext;
using grpc::Status;
using helloworld::Greeter;
using helloworld::HelloRequest;
using helloworld::HelloReply;

void handle_request(http::request<http::string_body>&& req, tcp::socket& socket) {
    // Create a response
    http::response<http::string_body> res{http::status::ok, req.version()};
    res.set(http::field::server, "Microservice");
    res.set(http::field::content_type, "text/plain");
    res.keep_alive(req.keep_alive());

    // Write the response
    res.body() = "Hello, world!\n";
    res.prepare_payload();
    http::write(socket, res);

    // Shutdown the socket
    beast::error_code ec;
    socket.shutdown(tcp::socket::shutdown_send, ec);
}

class GreeterServiceImpl final : public Greeter::Service {
  Status SayHello(ServerContext* context, const HelloRequest* request,
                  HelloReply* reply) override {
    std::cout << "GreeterServiceImpl handling HelloRequest: " << request->DebugString() << std::endl;
    std::string prefix("Hello, ");
    reply->set_message(prefix + request->name());
    return Status::OK;
  }
};


std::string getEnvOrDefault(const char* name, const std::string& defaultValue) {
    char* envValue = std::getenv(name);
    if (envValue == nullptr){
      return defaultValue;
    }
    return std::string(envValue);
}

void RunServer() {
  std::string port = getEnvOrDefault("SERVICE_PORT", "50051");
  std::string server_address = getEnvOrDefault("SERVICE_ADDRESS", "127.0.0.1");
  server_address.append(":");
  server_address.append(port);
  GreeterServiceImpl service;

  ServerBuilder builder;
  builder.AddListeningPort(server_address, grpc::InsecureServerCredentials());
  builder.RegisterService(&service);

  std::unique_ptr<Server> server(builder.BuildAndStart());
  std::cout << "Server listening on " << server_address << std::endl;
  server->Wait();
}

int main() {
    RunServer();
    try {
        // Create io_context and acceptor
        net::io_context io_context(1);
        tcp::acceptor acceptor(io_context, {tcp::v4(), 8080});

        // Main loop
        while (true) {
            // Accept a new connection
            tcp::socket socket(io_context);
            acceptor.accept(socket);

            // Read the request
            beast::flat_buffer buffer;
            http::request<http::string_body> req;
            http::read(socket, buffer, req);

            // Handle the request
            handle_request(std::move(req), socket);
        }
    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
        return 1;
    }

    return 0;
}
