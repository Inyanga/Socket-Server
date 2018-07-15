package com.cgalliance.server;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Session extends Thread {
    private BlockingQueue<Message> msgQueue;
    private Server serverRadiant;
    private Server serverDire;
    private Boolean isFull = false;
    private int sessionID;


    public Session(int sessionID) {
        this.sessionID = sessionID;
        msgQueue = new LinkedBlockingDeque<Message>();
    }

    @Override
    public void run() {
        waitForConnections();
        sessionProgress();
    }

    private void sessionProgress() {

        while (true) {

            try {

                Message message = msgQueue.take();
                System.out.println(" : : : " + message.getId() + " : : : ");
                if (message.getId().equals(serverRadiant.getThreadID())) {
                    System.out.println("Msg ID: " + message.getId() + " : msg for ThreadID " + serverDire.getThreadID());
                    serverDire.outputStream.println(Message.convertMsgToString(message));
                } else {
                    System.out.println("Msg ID " + message.getId() + " : ThreadID " + serverRadiant.getThreadID());
                    serverRadiant.outputStream.println(Message.convertMsgToString(message));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void waitForConnections() {
        while (!isFull) {
            try {
                System.out.println("Session " + sessionID + " is waiting");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Session is awake");
    }

    public synchronized void addConnection(Server server, Boolean isRadiant) {

        if (isRadiant) {
            serverRadiant = server;
            serverRadiant.setServerSettings(this, sessionID, msgQueue, true);

            System.out.println("--Radiant has been reconnected--");
        } else {
            serverDire = server;
            serverDire.setServerSettings(this, sessionID, msgQueue, false);

            System.out.println("--Dire has been reconnected--");
        }
        if (serverRadiant != null && serverDire != null) {
            System.out.println("ALL CLIENTS CONNECTED");
            isFull = true;

            sessionProgress();
            serverDire.serverThreadProgress();
            serverRadiant.serverThreadProgress();
            notifyAll();
        }
    }

    public synchronized void addConnection(Server server) {
        if (serverRadiant == null) {
            serverRadiant = server;
            serverRadiant.setServerSettings(this, sessionID, msgQueue, true);
            System.out.println("--Radiant connected--");
        } else {
            serverDire = server;
            serverDire.setServerSettings(Session.this, sessionID, msgQueue, false);

            System.out.println("--Dire connected--");
            System.out.println("--We've got two cliets connected--\n " +
                    "First ID " + serverRadiant.getThreadID() + "\n" +
                    "Second ID " + serverDire.getThreadID());
            isFull = true;
            notifyAll();
        }
    }

    public void notifySocketIsClosed(Socket socket) {
        isFull = false;
        waitForConnections();
    }

    public Boolean isFull() {
        return isFull;
    }


}
