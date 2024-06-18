package com.hdp.jpod;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.hdp.jpod.proto.StatusOuterClass;
import com.hdp.jpod.proto.StatusOuterClass.StatusCheckResponse;

public class DefaultHandler implements HttpHandler {
    private GrpcClient client;

    public GrpcClient getClient() {
        return client;
    }

    public DefaultHandler(GrpcClient client) {
        this.client = client;
    }

    private class Service {
        private ClusterService clusterService;
        private boolean isUp;

        Service(ClusterService clusterService, boolean isUp) {
            this.clusterService = clusterService;
            this.isUp = isUp;
        }
        
        String toJson() {
            return "{\"name\":\"" + getClusterService().getServiceName() + 
                 "\",\"isActive\":\"" + isUp()
                + "\"}";
        }

        ClusterService getClusterService() {
            return clusterService;
        }

        boolean isUp() {
            return isUp;
        }
    }

    private String getServiceStatusToJsonString(List<Service> services) {
            StringBuilder json = new StringBuilder();
            json.append("[");

            int i = 0;
            for (Service service : services) {
                json.append(service.toJson());
                if (i < services.size() - 1) {
                    json.append(",");
                }
                i++;
            }
            json.append("]");
            return json.toString();
    }

    private String handleStatusCheck() { // Check all available services here with ping request
        ArrayList<Service> services = new ArrayList<>();

        for (ClusterService service : ClusterService.values()) {
            if (service.isStatusCheckEnabled()) {
                StatusCheckResponse response = client.checkStatus(service);
                boolean isUp = false;
                if (response != null) {
                    isUp = (response.getStatus() == StatusOuterClass.StatusCheckResponse.ServiceStatus.OK);
                }
                services.add(new Service(service, isUp));
            }
        }
        
        return getServiceStatusToJsonString(services);
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
            LogHandler.getInstance().debug("Http server sent response");
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