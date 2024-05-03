package com.hdp.jpod;

import java.io.IOException;

public class MessageHandler implements IMessageHandler {
    private Client client; // To be used when sending new requests based on input

    public MessageHandler(Client client) {
        this.client = client;
    }
    
    public Response handleRequest(Request request) throws IOException {
        switch (request.getSender()) {
            case GREETINGS: // TODO. Currently no action, only immediate response
                return buildResponse("response from Java service to Go service!");

            default:
                return buildResponse("default response from Java service to an unknown service.");
        }
    }

    public void handleResponse(Response response) {
        if (response == null) {
            LogHandler.getInstance().warning("No response received");
            return;
        }
        LogHandler.getInstance().debug("Received Response: " + response.getContent() + " from: " + response.getSender());
    }

    public Response sendRequest(Request request) {
        LogHandler.getInstance().debug("Sending req: " + request.getContent() + " to: " + request.getRecipient() + " (" + request.getRecipient().getServicePort() + ")");
        return client.sendRequest("localhost", request.getRecipient().getServicePort(), request);
    }

    private Request buildRequest(ClusterService toService, String message) {
        return new MessageBuilder().setRecipient(toService).setContent(message).toRequest().build();
    }

    private Response buildResponse(String message) {
        return new MessageBuilder().setContent(message).toResponse().build();
    }
}