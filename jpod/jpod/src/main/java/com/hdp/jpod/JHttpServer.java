package com.hdp.jpod;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class JHttpServer implements IServer {

    private int httpPort;
    private HttpServer server;
    private boolean isUp;
    private GrpcClient client;

    public JHttpServer(int httpPort, GrpcClient client) {
        this.httpPort = httpPort;
        this.client = client;
        isUp = false;
    }

    @Override
    public void start() {
        if (server != null) {
            return;
        }

        try {
            server = HttpServer.create(new InetSocketAddress(httpPort), 0);
            server.createContext("/", new DefaultHandler(client));
            server.setExecutor(null);
            server.start();
            isUp = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogHandler.getInstance().info("Http server is ready on port: " + ClusterService.JPOD_HTTP.getServicePort());
    }

    @Override
    public void stop() {
        server.stop(httpPort);
        isUp = false;
    }

    @Override
    public boolean isUp() {
        return isUp;
    }

    @Override
    public void waitUntilReady() throws InterruptedException {
        while (!isUp()) {
            Thread.sleep(1);
        }
    }

    @Override
    public void awaitTermination() throws InterruptedException {
        while (isUp()) {
            Thread.sleep(10);
        }
    }   
}