package com.hdp.jpod;

public interface IServer {

    public void start();

    public void stop();

    public boolean isUp();

    public void waitUntilReady() throws InterruptedException;
    
    public void awaitTermination() throws InterruptedException;
}
