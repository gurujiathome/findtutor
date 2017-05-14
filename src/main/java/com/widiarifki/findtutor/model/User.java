package com.widiarifki.findtutor.model;

/**
 * Created by widiarifki on 11/05/2017.
 */

public class User {
    private int mId;
    private String mName;
    private String mEmail;
    private String mPhone;
    private int mIsTutor;
    private int mIsStudent;
    private int mIsProfileComplete;

    public int getIsTutor() {
        return mIsTutor;
    }

    public void setIsTutor(int isTutor) {
        this.mIsTutor = isTutor;
    }

    public int getIsStudent() {
        return mIsStudent;
    }

    public void setIsStudent(int isStudent) {
        this.mIsStudent = isStudent;
    }

    public int getIsProfileComplete() {
        return mIsProfileComplete;
    }

    public void setIsProfileComplete(int isProfileComplete) {
        mIsProfileComplete = isProfileComplete;
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

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }
}
