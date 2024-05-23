package com.hdp.jpod;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.nio.file.Files;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.hdp.jpod.proto.Helloworld.HelloReply;
import com.hdp.jpod.proto.Helloworld.HelloRequest;

public class DefaultHandler implements HttpHandler, IMessageHandler {
    private GrpcClient client;

    @Override
    public GrpcClient getClient() {
        return client;
    }

    public DefaultHandler(GrpcClient client) {
        this.client = client;
    }

    private class Service {
        private ClusterService clusterService;
        private boolean isUp;

        public Service(ClusterService clusterService, boolean isUp) {
            this.clusterService = clusterService;
            this.isUp = isUp;
        }
        
        public ClusterService getClusterService() {
            return clusterService;
        }

        public boolean isUp() {
            return isUp;
        }
    }

    private String getServiceStatus(List<Service> services) {
            StringBuilder json = new StringBuilder();
            json.append("[");

            int i = 0;
            for (Service service : services) {
                json.append("{\"name\":\"" + service.getClusterService().getServiceName() + 
                            "\",\"isActive\":\"" + service.isUp()
                        + "\"}");
                if (i < services.size() - 1) {
                    json.append(",");
                }
                i++;
            }
            json.append("]");
            return json.toString();
    }

    private String handleStatusCheck() { // Check all services here with ping request
        List<Service> services = new LinkedList<>();

        HelloReply cppReply = client.sayHello(ClusterService.CPPOD); // TODO change to status request
        boolean isUp = (cppReply != null);
        services.add(new Service(ClusterService.CPPOD, isUp));

        return getServiceStatus(services);
    }

    public String getResource(String request) throws IOException {
        switch (request) {
            case "/":
                return readFile("html/index.html");
            case "/styles/styles.css":
                return readFile("html/styles.css");
            case "/scripts/service_status.js":
                return readFile("html/service_status.js");
            case "/status_services":
                return handleStatusCheck();
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