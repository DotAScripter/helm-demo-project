package com.hdp.jpod;

public class Message {
    private ClusterService sender;
    private ClusterService recipient;
    private String fromAddr;
    private String content;

    public Message(String fromAddr, String content) {
        this.fromAddr = fromAddr;
        this.content = content;
    }

    public Message() {
        this.sender = null;
        this.recipient = null;
        this.fromAddr = "";
        this.content = "";
    }

    public ClusterService getSender() {
        return sender;
    }

    public void setSender(ClusterService sender) {
        this.sender = sender;
    }

    public ClusterService getRecipient() {
        return recipient;
    }

    public void setRecipient(ClusterService recipient) {
        this.recipient = recipient;
    }

    public Message(String fromAddr) {
        this.fromAddr = fromAddr;
        this.content = "";
    }

    public String getFromAddr() {
        return fromAddr;
    }

    public void setFromAddr(String fromAddr) {
        this.fromAddr = fromAddr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String string) {
        content = string;
    }
}