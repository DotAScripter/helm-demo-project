package com.hdp.jpod;

import java.io.IOException;
import java.net.UnknownHostException;

public class Client {
    public Response readResponse(Connection connection) throws IOException {
        return new MessageBuilder()
            .setFromAddr(connection.getAddr())
            .setContent(connection.getAllInput())
            .toResponse()
            .build();
    }

    public Response sendRequest(String addr, int port, Message message) {
        Response response = null;
        try {
            Connection connection = new Connection(addr, port);
            connection.send(message.getContent());
            LogHandler.getInstance().debug("Client sent message: " + message.getContent());
            response = readResponse(connection);

            connection.close(); // close connection after response is read
        } catch (UnknownHostException uh) {
            LogHandler.getInstance().error("Unknown host: " + uh.getMessage());
            return null;
        } catch (IOException io) {
            LogHandler.getInstance().error("Exception while handling request: " + io.getMessage());
            return null;
        }
        return response;
    }
}
