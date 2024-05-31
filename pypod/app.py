from flask import Flask
from concurrent import futures
import os
import logging
import sys
import grpc
import threading
from interface.status import status_pb2
from interface.status import status_pb2_grpc
from interface.helloworld import helloworld_pb2
from interface.helloworld import helloworld_pb2_grpc

logging.basicConfig(stream=sys.stdout, level=logging.INFO)

app = Flask(__name__)

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
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    status_pb2_grpc.add_StatusServicer_to_server(StatusServicer(), server)
    port = os.environ.get('STATUS_PORT', '50051')
    server.add_insecure_port('[::]:'+ port)
    server.start()
    logging.info("Server started on port:%s", port)
    server.wait_for_termination()

def serve_hello():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    helloworld_pb2_grpc.add_GreeterServicer_to_server(GreeterServicer(), server)
    port = os.environ.get('GREETER_PORT', '50052')
    server.add_insecure_port('[::]:'+ port)
    server.start()
    logging.info("Server started on port:%s", port)
    server.wait_for_termination()

if __name__ == "__main__":
    status_thread = threading.Thread(target=serve_status)
    status_thread.start()
    hello_thread = threading.Thread(target=serve_hello)
    hello_thread.start()
    from waitress import serve
    port = os.environ.get('HTTP_PORT', '3000')
    logging.info("Server listening on port:%s", port)
    serve(app, host="0.0.0.0", port=port)
    status_thread.join()
    hello_thread.join()
