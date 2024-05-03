package com.hdp.jpod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

//Connection to a client
public class Connection {
    private int port;
    private String addr;
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;

    public Connection(Socket clientSocket) {
        this.clientSocket = clientSocket;
        port = clientSocket.getLocalPort();
        addr = clientSocket.getInetAddress().getHostAddress();
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            LogHandler.getInstance().error("Exception while opening IOstreams to " + addr + ":" + port);
        }
    }

    public Connection(String addr, int port) throws IOException, UnknownHostException {
        this(new Socket(addr, port));
    }

    public String getInput() throws IOException {
        return reader.readLine();
    }

    public String getAllInput() throws IOException {
        String totalInput = "";
        String currentLine;
        while ((currentLine = getInput()) != null) {
            if (currentLine.length() == 0) {
                break;
            }
            totalInput += currentLine;
        }
       return totalInput;
    }

    public void send(String string) throws IOException {
        writer.println(string);
        writer.println(""); // empty string determines end of message
    }

    public void close() throws IOException {
        reader.close();
        writer.close();
        getClientSocket().close();
    }

    public int getPort() {
        return port;
    }

    public String getAddr() {
        return addr;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
