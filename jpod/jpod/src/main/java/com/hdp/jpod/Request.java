package com.hdp.jpod;

import java.io.IOException;

public class Request extends Message {
    private Connection connection;

    public void respond(Response response) throws IOException {
        response.setRecipient(getSender());
        response.setSender(ClusterService.JPOD);
        getConnection().send(response.getContent());
    }
   
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }
}
