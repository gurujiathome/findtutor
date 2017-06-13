package com.widiarifki.findtutor.model;

/**
 * Created by widiarifki on 10/06/2017.
 */

public class AvailabilityPerDay {
    private int mIdDay;
    private int mId;
    private String mStartHour;
    private String mEndHour;

    public AvailabilityPerDay(int id, int idDay, String startHour, String endHour){
        mId = id;
        mIdDay = idDay;
        mStartHour = startHour;
        mEndHour = endHour;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getDay() {
        return mIdDay;
    }

    public void setDay(int day) {
        mIdDay = day;
    }

    public String getStartHour() {
        return mStartHour;
    }

    public void setStartHour(String startHour) {
        this.mStartHour = startHour;
    }

    public String getEndHour() {
        return mEndHour;
    }

    public void setEndHour(String endHour) {
        this.mEndHour = endHour;
    }
}
