package com.hdp.jpod;

import java.io.IOException;

public interface IMessageHandler {
    public Response handleRequest(Request request) throws IOException;
    public void handleResponse(Response response);
}
