package com.cgalliance.server;

public class ReconnectStats {
    private String threadID;
    private Boolean isRadiant;
    private Session session;

    public ReconnectStats(String threadID, Boolean isRadiant, Session session) {
        this.threadID = threadID;
        this.isRadiant = isRadiant;
        this.session = session;
    }

    public String getThreadID() {
        return threadID;
    }

    public Boolean isRadiant() {
        return isRadiant;

    }

    public Session getSession() {
        return session;
    }
}
