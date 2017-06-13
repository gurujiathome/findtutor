package com.widiarifki.findtutor.model;

/**
 * Created by widiarifki on 04/06/2017.
 */

public class SubjectCategory implements Subject {
    private int mId;
    private String mName;

    public SubjectCategory(int id, String name) {
        this.mId = id;
        this.mName = name;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    @Override
    public boolean isSection() {
        return true;
    }
}
