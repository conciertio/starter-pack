import os
import redis
import socket

r = redis.Redis(host=os.environ["DB_HOST"], port=os.environ["DB_PORT"], decode_responses=True)
r.auth(os.environ["DB_PASSWORD"])

from http.server import BaseHTTPRequestHandler, HTTPServer # python3
class HandleRequests(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

    def do_GET(self):
        self._set_headers()
        host=os.environ["HOST_NAME"]
        r.incr(host)
        numhits = r.get(host)
        hits=f"hits on {host} -> {numhits}"
        self.wfile.write(bytes(hits,'UTF-8'))
        

host = ''
port = 8080
HTTPServer((host, port), HandleRequests).serve_forever()
