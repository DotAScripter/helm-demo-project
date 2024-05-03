package com.hdp.jpod;

public class Main {
    public static void main(String[] args) {
        IServer server = new Server(ClusterService.JPOD.getServicePort(), new MessageHandler(new Client()));
        server.start();

        try {
            server.waitUntilReady(); //wait until server is finished starting up
        } catch (InterruptedException ie) {
            LogHandler.getInstance().error("InterruptedException while waiting for server startup: " + ie.getMessage());
            System.exit(1);
        }
        //Server is now ready to receive messages
    }
}