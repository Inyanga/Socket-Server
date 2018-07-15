package com.cgalliance.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Server extends Thread {

    private static int threadCounter = 0;
    PrintWriter outputStream;
    private Socket socket;
    private BlockingQueue<Message> msgQueue;
    private List<Session> sessionList;
    private List<ReconnectStats> reconnectList;
    private Session currentSession;
    private String threadID;
    private final String CONNECTION_RESPONSE = "Connected. Your ID is " + threadID;
    private int sessionID;
    private Boolean isRadiant;
    private String incomingMsg;


    public Server(Socket socket, List<Session> sessionList, List<ReconnectStats> reconnectList) {
        this.socket = socket;
        this.sessionList = sessionList;
        this.reconnectList = reconnectList;
        System.out.println("--Client connected--");
    }


    @Override
    public void run() {
        serverThreadProgress();

    }

    void serverThreadProgress() {
        try {
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputStream = new PrintWriter(socket.getOutputStream(), true);

            while (true) {

                incomingMsg = inputStream.readLine();

                if (incomingMsg != null) {

                    Message message = Message.convertToMsg(incomingMsg);
                    if (message.getType() == 0) {
                        threadID = message.getId();
                        searchSession();
                        System.out.println("Initialize thread msg : " + threadID);
                    } else {
                        msgQueue.put(message);
                    }
                } else {
                    System.out.println("isRadiant " + isRadiant + " incomingMsg==null => closing socket");
                    closeSocket();
                }
            }
        } catch (IOException e) {
            System.out.println("--We've got some troubles--: " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("finally block => closing socket");
            closeSocket();
        }
    }

    private synchronized void closeSocket() {
        try {
            reconnectList.add(new ReconnectStats(threadID, isRadiant, currentSession));
            currentSession.notifySocketIsClosed(socket);
            socket.close();
            System.out.println("--Socket is closed--");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchSession() {
        Session lastSession;
        if (sessionList.size() > 0) {
            if (reconnectList.size() > 0) {
                for (ReconnectStats reconnectStats : reconnectList) {
                    if (reconnectStats.getThreadID() != null && reconnectStats.getThreadID().equals(threadID)) {
                        System.out.println("FIND WAITING THREAD # # # ");
                        reconnectStats.getSession().addConnection(this, reconnectStats.isRadiant());
                        System.out.println("Client reconnected " + "isRadiant: " + reconnectStats.isRadiant());
                        reconnectList.remove(reconnectStats);

                        return;
                    }
                }
            }
            lastSession = sessionList.get(sessionList.size() - 1);
            if (lastSession.isFull()) {
                onCreateSession(this);
            } else {
                lastSession.addConnection(this);
            }
        } else {
            onCreateSession(this);
        }
    }

    private synchronized void onCreateSession(Server newServer) {
        Session newSession = new Session(sessionList.size());
        sessionList.add(newSession);
        newSession.start();
        newSession.addConnection(newServer);
        System.out.println("New session created");
    }

    void setServerSettings(Session session, int sessionID, BlockingQueue<Message> msgQueue, Boolean isRadiant) {
        this.currentSession = session;
        this.sessionID = sessionID;
        this.msgQueue = msgQueue;
        this.isRadiant = isRadiant;
    }


    String getThreadID() {
        return threadID;
    }

    Socket getSocket() {
        return socket;
    }

    Boolean isSocketClosed() {
        return socket.isClosed();
    }
}
