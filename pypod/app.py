from flask import Flask
from concurrent import futures
import os
import logging
import sys
import grpc
import threading
import signal
from interface.status import status_pb2
from interface.status import status_pb2_grpc
from interface.helloworld import helloworld_pb2
from interface.helloworld import helloworld_pb2_grpc

logging.basicConfig(stream=sys.stdout, level=logging.INFO)

app = Flask(__name__)

def handle_sigterm(signal_number, frame):
    logging.info("Received SIGTERM signal")
    if hasattr(serve_status, 'server'):
        serve_status.server.stop(None)
    if hasattr(serve_hello, 'server'):
        serve_hello.server.stop(None)
    http_server_thread.stop()

@app.route("/")
def index():
    logging.info("Got request on /")
    return "Hello world!"

class StatusServicer(status_pb2_grpc.StatusServicer):
    def CheckStatus(self, request, context):
        logging.info("Got StatusCheckRequest:%s", request.service_name)
        return status_pb2.StatusCheckResponse(status=status_pb2.StatusCheckResponse.OK)
    
class GreeterServicer(helloworld_pb2_grpc.GreeterServicer):
    def SayHello(self, request, context):
        logging.info("Got HelloRequest:%s", request.name)
        return helloworld_pb2.HelloReply(message="Hello from Pyapp")

def serve_status():
    server = None
    try:
        server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
        status_pb2_grpc.add_StatusServicer_to_server(StatusServicer(), server)
        port = os.environ.get('STATUS_PORT', '50051')
        server.add_insecure_port('[::]:'+ port)
        server.start()
        logging.info("Server started on port:%s", port)
        serve_status.server = server
        server.wait_for_termination()
    except Exception as e:
        logging.error("Error starting server", exc_info=True)
    finally:
        if server:
            server.stop(None)

def serve_hello():
    server = None
    try:
        server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
        helloworld_pb2_grpc.add_GreeterServicer_to_server(GreeterServicer(), server)
        port = os.environ.get('GREETER_PORT', '50052')
        server.add_insecure_port('[::]:'+ port)
        server.start()
        logging.info("Server started on port:%s", port)
        serve_hello.server = server
        server.wait_for_termination()
    except Exception as e:
        logging.error("Error starting server", exc_info=True)
    finally:
        if server:
            server.stop(None)

class WaitressServerThread(threading.Thread):
    def __init__(self):
        super().__init__()
        self._stop_event = threading.Event()
        self.server = None

    def run(self):
        from waitress import create_server
        port = os.environ.get('HTTP_PORT', '3000')
        logging.info("Server listening on port:%s", port)
        self.server = create_server(app, host='0.0.0.0', port=8080)
        self.server.run()

    def stop(self):
        if self.server:
            self.server.close()
        self._stop_event.set()


if __name__ == "__main__":
    logging.info("Starting")
    signal.signal(signal.SIGTERM, handle_sigterm)
    status_thread = threading.Thread(target=serve_status)
    status_thread.start()
    hello_thread = threading.Thread(target=serve_hello)
    hello_thread.start()
    http_server_thread = WaitressServerThread()
    http_server_thread.start()
    status_thread.join()
    hello_thread.join()
    http_server_thread.join()
    logging.info("Good bye")
