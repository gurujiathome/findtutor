package com.widiarifki.findtutor.model;

/**
 * Created by widiarifki on 09/06/2017.
 */

public class Day {
    private String mName;
    private int mId;

    public Day(int id, String name){
        mName = name;
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
