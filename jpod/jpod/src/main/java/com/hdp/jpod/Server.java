package com.hdp.jpod;

import java.io.IOException;
import java.net.ServerSocket;

public class Server implements IServer {
    private int serverPortNumber;
    private IMessageHandler messageHandler;
    private boolean isUp;
    private ServerSocket serverSocket;
    private Thread serverThread;

    public class ServerRunnable implements Runnable {
        private ServerSocket serverSocket;
        public ServerRunnable(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }
        @Override
        public void run() {
            waitForConnections(serverSocket);
        }
    }

    public Server(int serverPortNumber, IMessageHandler messageHandler) {
        this.serverPortNumber = serverPortNumber;
        this.messageHandler = messageHandler;
    }

    public boolean isUp() {
        return isUp;
    }
    
    public void waitUntilReady() throws InterruptedException {
        while (!isUp()) {
                Thread.sleep(1);
        }
    }

    public void start() {
        if (serverSocket != null) {
            LogHandler.getInstance().warning("Server already started");
            return;
        }

        LogHandler.getInstance().debug("Starting server. Listening on port: " + serverPortNumber);
        new Thread(() -> { 
            try {
                serverSocket = new ServerSocket(serverPortNumber);
                serverThread = new Thread(new ServerRunnable(serverSocket));
                serverThread.start();
                LogHandler.getInstance().info("Jpod service is up");
                isUp = true;

                serverThread.join(); // wait for server thread to finish before continuing
                serverSocket.close();
            } catch (IOException e) {
                LogHandler.getInstance().error("IO exception received on server socket: " + e.getMessage());
                return;
            } catch (InterruptedException ie) {
                LogHandler.getInstance().error("Server thread interrupted: " + ie.getMessage());
            }
        }).start();
    }

    public void stop() {
        if (serverSocket == null) {
            LogHandler.getInstance().warning("Server already stopped");
            return;
        }
        LogHandler.getInstance().debug("Attempting to stop server");
        try {
            serverSocket.close();
            serverThread.join();
            isUp = false;
            serverSocket = null;
            LogHandler.getInstance().info("Jpod service stopped");    
        } catch (IOException io) {
            LogHandler.getInstance().error("IO exception received on server socket while closing: " + io.getMessage());
            System.exit(1);
        } catch (InterruptedException ie) {
            LogHandler.getInstance().error("Server thread interrupted while closing: " + ie.getMessage());
            System.exit(1);
        }
    }

    private Request readRequest(Connection connection) throws IOException {
        return new MessageBuilder()
            .setFromAddr(connection.getAddr())
            .setContent(connection.getAllInput())
            .setSender(getSender(connection))
            .toRequest()
            .setConnection(connection)
            .build();
    }

    
    private ClusterService getSender(Connection connection) {
        //TODO get sender information from the message contents
        return ClusterService.UNKNOWN; // use this as default until sender service has added this field
    }
    

    private void waitForConnections(ServerSocket serverSocket) {
        while (true) {
            try {
                Connection connection = new Connection(serverSocket.accept());
                new Thread(() -> { // handle client connection
                    LogHandler.getInstance().debug("Client connected to port: " + connection.getPort() + " from: " + connection.getAddr());
                    try {
                        Request incomingRequest = readRequest(connection);
                        LogHandler.getInstance().debug("Received Request: " + incomingRequest.getContent() + " from: " + incomingRequest.getSender());

                        Response outgoingResponse = messageHandler.handleRequest(incomingRequest);
                        incomingRequest.respond(outgoingResponse);

                        LogHandler.getInstance().debug("Sent response: " + outgoingResponse.getContent() + " to: " + outgoingResponse.getRecipient());
                        connection.close();
                    } catch (IOException e) {
                        LogHandler.getInstance().error("Exception while handling client message: " + e.getMessage());
                    }
                }).start();
            } catch (IOException io) {
                LogHandler.getInstance().debug("Exception on server socket: " + io.getMessage());
                return;
            }
        }
    }
}