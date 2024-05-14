import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import org.junit.Test;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hdp.jpod.ClusterService;
import com.hdp.jpod.GrpcClient;
import com.hdp.jpod.GrpcServer;
import com.hdp.jpod.IServer;
import com.hdp.jpod.LogHandler;
import com.hdp.jpod.proto.Helloworld.HelloReply;


public class TestClientServer {
    @Test
    public void testRequestResponse() throws InvalidProtocolBufferException {
        GrpcClient client = new GrpcClient();
        IServer server = null;

        server = new GrpcServer(ClusterService.JPOD.getServicePort());
        server.start();

        try {
            server.waitUntilReady(); //wait until server is finished starting up
        } catch (InterruptedException ie) {
            LogHandler.getInstance().error("InterruptedException while waiting for server startup: " + ie.getMessage());
            fail();
        }
        assertTrue(server.isUp());
    
        HelloReply response = client.sayHello(ClusterService.JPOD);
        
        assertEquals("default reply", response.getMessage());
        server.stop();
        assertFalse(server.isUp());

    } 
    
}
