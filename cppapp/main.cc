#include <boost/beast/core.hpp>
#include <boost/beast/http.hpp>
#include <boost/asio.hpp>
#include <iostream>

namespace beast = boost::beast;
namespace http = beast::http;
namespace net = boost::asio;

using tcp = net::ip::tcp;

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

int main() {
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