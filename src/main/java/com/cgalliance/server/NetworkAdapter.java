package com.cgalliance.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class NetworkAdapter {

    private List<Session> sessionList;
    private List<ReconnectStats> reconnectList;
    private int port;

    public NetworkAdapter(int port) {
        this.port=port;
        sessionList = new ArrayList<Session>();
        reconnectList = new ArrayList<ReconnectStats>();
    }

    public void startServer() {
        Session lastSession;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!serverSocket.isClosed()) {
                Server newServer = new Server(serverSocket.accept(), sessionList, reconnectList);
                newServer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
