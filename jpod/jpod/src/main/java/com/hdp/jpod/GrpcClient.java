package com.hdp.jpod;

import java.util.concurrent.TimeUnit;

import com.hdp.jpod.proto.StatusGrpc;
import com.hdp.jpod.proto.GreeterGrpc;
import com.hdp.jpod.proto.GreeterGrpc.GreeterBlockingStub;
import com.hdp.jpod.proto.Helloworld.HelloReply;
import com.hdp.jpod.proto.Helloworld.HelloRequest;
import com.hdp.jpod.proto.StatusGrpc.StatusBlockingStub;
import com.hdp.jpod.proto.StatusOuterClass.StatusCheckRequest;
import com.hdp.jpod.proto.StatusOuterClass.StatusCheckResponse;

import java.time.Duration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class GrpcClient {
    private final static Duration DEFAULT_TIMEOUT = Duration.ofSeconds(5);

    private ManagedChannel getChannel(ClusterService clusterService) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(clusterService.getServiceIP(), clusterService.getServicePort())
            .usePlaintext()
            .build();
            return channel;
    }

    public StatusCheckResponse checkStatus(ClusterService targetService) {
        ManagedChannel channel = null;
        try {
            channel = getChannel(targetService);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        StatusBlockingStub blockingStub = StatusGrpc.newBlockingStub(channel).withDeadlineAfter(DEFAULT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);        
        StatusCheckRequest request = StatusCheckRequest.newBuilder().build();

        StatusCheckResponse reply = null;
        try {
            reply = blockingStub
                .withDeadlineAfter(DEFAULT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .checkStatus(request);
            LogHandler.getInstance().debug("Client sent statusCheckReq to: " + targetService.getServiceName());
            LogHandler.getInstance().debug("Client got response from: " + targetService.getServiceName() + ": " + reply.getStatus());
        } catch (StatusRuntimeException e) {
            LogHandler.getInstance().error("Error encountered when sending statusCheck to: " + targetService.getServiceName() + ". Reason: " + e.getMessage());
        } finally {
            channel.shutdown();
        }
        return reply;
    }

    public HelloReply sayHello(ClusterService targetService) {
        ManagedChannel channel = null;
        try {
            channel = getChannel(targetService);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel).withDeadlineAfter(DEFAULT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);        
        HelloRequest request = HelloRequest.newBuilder()
            .setName("Jpod")
            .build();

        HelloReply reply = null;
        try {
            reply = blockingStub
                .withDeadlineAfter(DEFAULT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)
                .sayHello(request);
            LogHandler.getInstance().debug("Client sent message: " + request.getName());
            LogHandler.getInstance().debug("Client got response: " + reply.getMessage());
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        } finally {
            channel.shutdown();
        }
        return reply;
    }
}
