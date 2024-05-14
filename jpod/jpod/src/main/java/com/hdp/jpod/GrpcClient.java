package com.hdp.jpod;

import com.hdp.jpod.proto.GreeterGrpc;
import com.hdp.jpod.proto.GreeterGrpc.GreeterBlockingStub;
import com.hdp.jpod.proto.Helloworld.HelloReply;
import com.hdp.jpod.proto.Helloworld.HelloRequest;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {

    public HelloReply sayHello(ClusterService targetService) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", targetService.getServicePort())
            .usePlaintext()
            .build();
            GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);

            HelloRequest request = HelloRequest.newBuilder()
                .setName("Jpod")
                .build();

            HelloReply reply = blockingStub.sayHello(request);
            LogHandler.getInstance().debug("Client sent message: " + request.getName());
            LogHandler.getInstance().debug("Client got response: " + reply.getMessage());
            channel.shutdown();

            return reply;
    }
}
