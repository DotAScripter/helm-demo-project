package com.hdp.jpod;

public class MessageBuilder {
    private Message message;
    public MessageBuilder() {
        message = new Message();
    }

    public class RequestBuilder extends MessageBuilder {
        private Request request;
        
        public RequestBuilder(Message message) {
            request = new Request();
            request.setContent(message.getContent());
            request.setFromAddr(message.getFromAddr());
            request.setSender(message.getSender());
            request.setRecipient(message.getRecipient());
        }
        
        public RequestBuilder setConnection(Connection connection) {
            request.setConnection(connection);
            return this;
        }
    
        @Override
        public Request build() {
            return request;
        }
    }

    public class ResponseBuilder extends MessageBuilder {
        private Response response;
        
        public ResponseBuilder(Message message) {
            response = new Response();
            response.setContent(message.getContent());
            response.setFromAddr(message.getFromAddr());
            response.setSender(message.getSender());
            response.setRecipient(message.getRecipient());
        }
    
        @Override
        public Response build() {
            return response;
        }
    }

    public MessageBuilder setSender(ClusterService sender) {
        message.setSender(sender);
        return this;
    }

    public MessageBuilder setRecipient(ClusterService recipient) {
        message.setRecipient(recipient);
        return this;
    }


    public MessageBuilder setContent(String string) {
        message.setContent(string);
        return this;
    }

    public MessageBuilder appendContent(String string) {
        message.setContent(message.getContent() + string);
        return this;
    }

    public MessageBuilder setFromAddr(String fromAddr) {
        message.setFromAddr(fromAddr);
        return this;
    }

    public Message build() {
        return message;
    }

    public RequestBuilder toRequest() {
        return new RequestBuilder(message);
    }

    public ResponseBuilder toResponse() {
        return new ResponseBuilder(message);
    }
}
