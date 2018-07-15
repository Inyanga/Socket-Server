package com.cgalliance.server;

import java.io.Serializable;

public class Message implements Serializable {
    private String id;
    private String msg;
    private int type;

    public Message(String id, String msg, int type) {
        super();
        this.id = id;
        this.msg = msg;
        this.type = type;
    }

    public static Message convertToMsg(String msg) {
        String[] fields = msg.split("#");
        return new Message(
                fields[0],
                fields[1],
                Integer.parseInt(fields[2])
        );
    }

    public static String convertMsgToString(Message msg) {
        final String SEPARATOR = "#";
        String id = msg.getId();
        String msgBody = msg.getMsg();
        String type = Integer.toString(msg.getType());
        return id + SEPARATOR + msgBody + SEPARATOR + type;
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

}
