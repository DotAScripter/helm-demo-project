package com.hdp.jpod;

import java.util.ArrayList;

public class Main {
    private static void stopServices(ArrayList<IServer> serviceList) {
        for (IServer server : serviceList) {
            server.stop();
        }
    }

    private static void awaitServices(ArrayList<IServer> serviceList)  {
        try {
            for (IServer server : serviceList) {
                server.awaitTermination();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void startServices(ArrayList<IServer> serviceList) {
        for (IServer server : serviceList) {
            server.start();
            try {
                server.waitUntilReady(); //wait until server is finished starting up
            } catch (InterruptedException ie) {
                LogHandler.getInstance().error("InterruptedException while waiting for grpc server startup: " + ie.getMessage());
                System.exit(1);
            }
        }
    }

    public static ArrayList<IServer> getServiceList() {
        ArrayList<IServer> grpcServiceList = new ArrayList<>();
        grpcServiceList.add(new GreeterServer(ClusterService.JPOD_GREETER.getServicePort()));
        grpcServiceList.add(new StatusServer(ClusterService.JPOD_STATUS.getServicePort()));
        return grpcServiceList;
    }

    public static void main(String[] args) {
        ArrayList<IServer> grpcServiceList = getServiceList();
        IServer httpServer = new JHttpServer(ClusterService.JPOD_HTTP.getServicePort(), new GrpcClient());

        // Start services for contact with other grpc services
        startServices(grpcServiceList);

        // Start http server
        httpServer.start();
        try {
            httpServer.waitUntilReady();
        } catch (InterruptedException ie) {
            LogHandler.getInstance().error("InterruptedException while waiting for http server startup: " + ie.getMessage() + ". Exiting...");
            stopServices(grpcServiceList);
            System.exit(1);
        }

        //Server is now ready to receive messages            
        LogHandler.getInstance().info("Jpod service is ready!");

        awaitServices(grpcServiceList);

        httpServer.stop();
        LogHandler.getInstance().info("Jpod service shutting down.");
    }
}