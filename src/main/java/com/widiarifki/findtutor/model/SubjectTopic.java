package com.widiarifki.findtutor.model;

/**
 * Created by widiarifki on 04/06/2017.
 */

public class SubjectTopic implements Subject {
    private int mIdCategory;
    private int mId;
    private String mName;

    public SubjectTopic(int idCategory, int id, String name) {
        this.mIdCategory = idCategory;
        this.mId = id;
        this.mName = name;
    }

    public int getIdCategory() {
        return mIdCategory;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    @Override
    public boolean isSection() {
        return false;
    }

}

