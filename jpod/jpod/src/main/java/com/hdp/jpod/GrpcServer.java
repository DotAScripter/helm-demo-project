package com.hdp.jpod;

import java.io.IOException;

import com.hdp.jpod.proto.GreeterGrpc.GreeterImplBase;
import com.hdp.jpod.proto.Helloworld.HelloReply;
import com.hdp.jpod.proto.Helloworld.HelloRequest;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerMethodDefinition;
import io.grpc.stub.StreamObserver;

public class GrpcServer extends GreeterImplBase implements IServer {
    private Server server;
    private boolean isUp;

    public GrpcServer() {
    }
    public GrpcServer(int serverPortNumber) {
        server = ServerBuilder.forPort(serverPortNumber)
            .addService(new GrpcServer())  
            .build();
     }

    public void start() {
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isUp = true;
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
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder()
            .setMessage("default reply")
            .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}