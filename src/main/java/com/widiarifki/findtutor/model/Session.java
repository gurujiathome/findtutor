package com.widiarifki.findtutor.model;

import android.location.Location;

/**
 * Created by widiarifki on 09/07/2017.
 */

public class Session {

    int mId;
    String mScheduleDate;
    String mStartHour;
    String mEndHour;
    int mIsScheduleAccepted;
    int mStatus;
    String mLatitude;
    String mLongitude;
    Location mLocation;
    String mLocationAddress;
    double mDistanceBetween;
    User mUser;
    String mSubject;
    String mDateHeld;
    String mStartHourHeld;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getScheduleDate() {
        return mScheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        mScheduleDate = scheduleDate;
    }

    public String getStartHour() {
        return mStartHour;
    }

    public void setStartHour(String startHour) {
        mStartHour = startHour;
    }

    public String getEndHour() {
        return mEndHour;
    }

    public void setEndHour(String endHour) {
        mEndHour = endHour;
    }

    public int getIsScheduleAccepted() {
        return mIsScheduleAccepted;
    }

    public void setIsScheduleAccepted(int isScheduleAccepted) {
        mIsScheduleAccepted = isScheduleAccepted;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }

    public Location getLocation() {
        if(mLatitude != null && !mLatitude.isEmpty() && mLongitude != null && !mLongitude.isEmpty()) {
            Location location = new Location("");
            location.setLatitude(Double.parseDouble(mLatitude));
            location.setLongitude(Double.parseDouble(mLongitude));
            return location;
        }else{
            return null;
        }
    }

    public String getLocationAddress() {
        return mLocationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        mLocationAddress = locationAddress;
    }

    public double getDistanceBetween() {
        return mDistanceBetween;
    }

    public void setDistanceBetween(double distanceBetween) {
        mDistanceBetween = distanceBetween;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public String getDateHeld() {
        return mDateHeld;
    }

    public void setDateHeld(String dateHeld) {
        mDateHeld = dateHeld;
    }

    public String getStartHourHeld() {
        return mStartHourHeld;
    }

    public void setStartHourHeld(String startHourHeld) {
        mStartHourHeld = startHourHeld;
    }
}
