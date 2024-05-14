package com.hdp.jpod;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class JHttpServer implements IServer {

    private int httpPort;
    private HttpServer server;
    private boolean isUp;

    public JHttpServer(int httpPort) {
        this.httpPort = httpPort;
        isUp = false;
    }

    @Override
    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(httpPort), 0);
            server.createContext("/hello", new DefaultHandler());
            server.setExecutor(null);
            server.start();
            isUp = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
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