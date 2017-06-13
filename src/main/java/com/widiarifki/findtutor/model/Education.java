package com.widiarifki.findtutor.model;

/**
 * Created by widiarifki on 30/05/2017.
 */

public class Education {

    int mId;
    int mIdUser;
    String mYearGraduate;
    int mSchoolLevel;
    String mSchoolLevelText;
    String mSchoolName;
    String mDepartment;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getIdUser() {
        return mIdUser;
    }

    public void setIdUser(int idUser) {
        mIdUser = idUser;
    }

    public String getYearGraduate() {
        return mYearGraduate;
    }

    public void setYearGraduate(String yearGraduate) {
        mYearGraduate = yearGraduate;
    }

    public int getSchoolLevel() {
        return mSchoolLevel;
    }

    public void setSchoolLevel(int schoolLevel) {
        mSchoolLevel = schoolLevel;
    }

    public String getSchoolLevelText() {
        return mSchoolLevelText;
    }

    public void setSchoolLevelText(String schoolLevelText) {
        mSchoolLevelText = schoolLevelText;
    }

    public String getSchoolName() {
        return mSchoolName;
    }

    public void setSchoolName(String schoolName) {
        mSchoolName = schoolName;
    }

    public String getDepartment() {
        return mDepartment;
    }

    public void setDepartment(String department) {
        mDepartment = department;
    }
}
