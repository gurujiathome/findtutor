package com.widiarifki.findtutor.model;

import java.util.Date;

/**
 * Created by widiarifki on 26/07/2017.
 */

public class Complain {

    User mUser;
    Session mSession;
    String mComplain;
    String mReply;
    Date mCreatedTime;
    Date mLastUpdate;

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public Session getSession() {
        return mSession;
    }

    public void setSession(Session session) {
        mSession = session;
    }

    public String getComplain() {
        return mComplain;
    }

    public void setComplain(String complain) {
        mComplain = complain;
    }

    public String getReply() {
        return mReply;
    }

    public void setReply(String reply) {
        mReply = reply;
    }

    public Date getCreatedTime() {
        return mCreatedTime;
    }

    public void setCreatedTime(Date createdTime) {
        mCreatedTime = createdTime;
    }

    public Date getLastUpdate() {
        return mLastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        mLastUpdate = lastUpdate;
    }
}
