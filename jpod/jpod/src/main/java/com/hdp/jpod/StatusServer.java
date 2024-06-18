package com.hdp.jpod;

import java.io.IOException;

import com.hdp.jpod.proto.StatusGrpc.StatusImplBase;
import com.hdp.jpod.proto.StatusOuterClass;
import com.hdp.jpod.proto.StatusOuterClass.StatusCheckRequest;
import com.hdp.jpod.proto.StatusOuterClass.StatusCheckResponse;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class StatusServer extends StatusImplBase implements IServer {
    private Server server;
    private boolean isUp;

    public StatusServer() {
    }
    
    public StatusServer(int serverPortNumber) {
        server = ServerBuilder.forPort(serverPortNumber)
            .addService(new StatusServer())  
            .build();
     }

    public void start() {
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isUp = true;
        LogHandler.getInstance().info("Status server is ready on port: " + ClusterService.JPOD_STATUS.getServicePort());
    }

    public void stop() {
        server.shutdown();
        isUp = false;
    }

    public boolean isUp() {
        return isUp;
    }

    public void waitUntilReady() throws InterruptedException {
        while (!isUp()) {
            Thread.sleep(1);
        }
    }

    public void awaitTermination() throws InterruptedException {
        server.awaitTermination();
    }

    @Override
    public void checkStatus(StatusCheckRequest request, StreamObserver<StatusCheckResponse> responseObserver) {
        StatusCheckResponse reply = StatusCheckResponse.newBuilder()
            .setStatus(StatusOuterClass.StatusCheckResponse.ServiceStatus.OK)
            .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}

