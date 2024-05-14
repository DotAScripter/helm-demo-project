from flask import Flask
import os
import logging
import sys

logging.basicConfig(stream=sys.stdout, level=logging.INFO)

app = Flask(__name__)

@app.route("/")
def index():
    logging.info("Got request on /")
    return "Hello world!"

if __name__ == "__main__":
    from waitress import serve
    port = os.environ.get('HTTP_PORT', '3000')
    logging.info("Server listening on port:%s", port)
    serve(app, host="0.0.0.0", port=port)
