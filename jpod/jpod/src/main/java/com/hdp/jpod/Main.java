package com.hdp.jpod;

public class Main {
    public static void main(String[] args) {
        IServer server = new GrpcServer(ClusterService.JPOD.getServicePort());
        IServer httpServer = new JHttpServer(ClusterService.JPODHTTP.getServicePort(), new GrpcClient());

        // Start server for contact with other grpc services
        server.start();
        try {
            server.waitUntilReady(); //wait until server is finished starting up
        } catch (InterruptedException ie) {
            LogHandler.getInstance().error("InterruptedException while waiting for grpc server startup: " + ie.getMessage());
            System.exit(1);
        }
        LogHandler.getInstance().info("Grpc server is ready on port: " + ClusterService.JPOD.getServicePort());
        
        // Start http server
        httpServer.start();
        try {
            httpServer.waitUntilReady();
        } catch (InterruptedException ie) {
            LogHandler.getInstance().error("InterruptedException while waiting for http server startup: " + ie.getMessage());
            server.stop();
            System.exit(1);
        }
        LogHandler.getInstance().info("Http server is ready on port: " + ClusterService.JPODHTTP.getServicePort());

        //Server is now ready to receive messages            
        LogHandler.getInstance().info("Jpod service is ready!");

        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        httpServer.stop();
        LogHandler.getInstance().info("Jpod service shutting down.");
    }
}