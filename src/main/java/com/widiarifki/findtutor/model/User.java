package com.widiarifki.findtutor.model;

import java.util.HashMap;
import java.util.List;

/**
 * Created by widiarifki on 11/05/2017.
 */

public class User {
    private int mId;
    private String mName;
    private String mEmail;
    private int mGender;
    private String mPhone;
    private String mPhotoUrl;
    private int mIsTutor;
    private int mIsStudent;
    private int mIsProfileComplete;
    private String mBio;
    private int mMaxTravelDistance;
    private int mMinPriceRate;
    private List<Education> mEducations;
    private HashMap<String, SavedSubject> mSubjects;
    private int mIsAvailable;
    private HashMap<String, List<AvailabilityPerDay>> mAvailabilities;
    private String mDeviceId;
    private String mLatitude;
    private String mLongitude;
    private String mLocationAddress;
    private double mDistanceFromRequestor;

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

    public int getGender() {
        return mGender;
    }

    public void setGender(int gender) {
        mGender = gender;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) { mPhone = phone; }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        mPhotoUrl = photoUrl;
    }

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

    public void setIsProfileComplete(int isProfileComplete) { mIsProfileComplete = isProfileComplete; }

    public String getBio() {
        return mBio;
    }

    public void setBio(String bio) {
        mBio = bio;
    }

    public int getMaxTravelDistance() {
        return mMaxTravelDistance;
    }

    public void setMaxTravelDistance(int maxTravelDistance) {
        mMaxTravelDistance = maxTravelDistance;
    }

    public int getMinPriceRate() {
        return mMinPriceRate;
    }

    public void setMinPriceRate(int minPriceRate) {
        mMinPriceRate = minPriceRate;
    }

    public List<Education> getEducations() {
        return mEducations;
    }

    public void setEducations(List<Education> educations) {
        mEducations = educations;
    }

    public HashMap<String, SavedSubject> getSubjects() {
        return mSubjects;
    }

    public void setSubjects(HashMap<String, SavedSubject> subjects) {
        mSubjects = subjects;
    }

    public int getIsAvailable() {
        return mIsAvailable;
    }

    public void setIsAvailable(int isAvailable) {
        mIsAvailable = isAvailable;
    }

    public HashMap<String, List<AvailabilityPerDay>> getAvailabilities() {
        return mAvailabilities;
    }

    public void setAvailabilities(HashMap<String, List<AvailabilityPerDay>> availabilities) {
        mAvailabilities = availabilities;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String deviceId) {
        mDeviceId = deviceId;
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

    public String getLocationAddress() {
        return mLocationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        mLocationAddress = locationAddress;
    }

    public double getDistanceFromRequestor() {
        return mDistanceFromRequestor;
    }

    public void setDistanceFromRequestor(double distanceFromRequestor) {
        mDistanceFromRequestor = distanceFromRequestor;
    }
}
