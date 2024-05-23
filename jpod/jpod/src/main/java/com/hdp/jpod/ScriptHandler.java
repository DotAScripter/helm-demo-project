package com.hdp.jpod;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Files;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ScriptHandler implements HttpHandler {

    public String getResource(String request) throws IOException {
        switch (request) {
            case "scripts/service_status.js":
                return readFile("html/service_status.js");
            default:
                break;
        }
        return null;
    }
     @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestUri = exchange.getRequestURI().toString();
            String response = getResource(requestUri);

            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            LogHandler.getInstance().debug("Http server sent response: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFile(String fileName) throws IOException {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
                byte[] fileBytes = inputStream.readAllBytes();
                return new String(fileBytes);
            }
        }
}