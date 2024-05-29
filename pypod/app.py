from flask import Flask
from concurrent import futures
import os
import logging
import sys
import grpc
import threading
from interface.status import status_pb2
from interface.status import status_pb2_grpc

logging.basicConfig(stream=sys.stdout, level=logging.INFO)

app = Flask(__name__)

@app.route("/")
def index():
    logging.info("Got request on /")
    return "Hello world!"

class StatusServicer(status_pb2_grpc.StatusServicer):
    def CheckStatus(self, request, context):
        logging.info("Server listening on port:%s", request.service_name)
        return status_pb2.StatusCheckResponse(status=status_pb2.StatusCheckResponse.OK)

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    status_pb2_grpc.add_StatusServicer_to_server(StatusServicer(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    logging.info("Server started on port:50051")
    server.wait_for_termination()

if __name__ == "__main__":
    gprc_thread = threading.Thread(target=serve)
    gprc_thread.start()
    from waitress import serve
    port = os.environ.get('HTTP_PORT', '3000')
    logging.info("Server listening on port:%s", port)
    serve(app, host="0.0.0.0", port=port)
    gprc_thread.join()
