package com.widiarifki.findtutor.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.widiarifki.findtutor.model.AvailabilityPerDay;
import com.widiarifki.findtutor.model.Education;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by widiarifki on 12/05/2017.
 */

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context mContext;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "sharedPref";

    // All Shared Preferences Keys
    private static final String KEY_IS_LOGIN = "isLoggedIn";
    public static final String KEY_ID_USER = "id_user";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_PHOTO_URL = "photo_url";
    public static final String KEY_IS_TUTOR = "is_tutor";
    public static final String KEY_IS_STUDENT = "is_student";
    public static final String KEY_IS_PROFILE_COMPLETE = "is_profile_complete";
    public static final String KEY_BIO = "bio";
    public static final String KEY_MAX_TRAVEL_DISTANCE = "max_travel_distance";
    public static final String KEY_MIN_PRICE_RATE = "min_price_rate";
    public static final String KEY_EDUCATIONS = "educations";
    public static final String KEY_SUBJECTS = "subjects";
    public static final String KEY_IS_AVAILABLE = "is_available";
    public static final String KEY_TIMESLOTS = "timeslots";
    public static final String KEY_AVAILABILITIES = "availabilities";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_DEVICE_ID = "device_id";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_LOCATION_ADDRESS = "location_address";

    // Constructor
    public SessionManager(Context context){
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGIN, false);
    }

    public void updateSession(User user){
        editor.putBoolean(KEY_IS_LOGIN, true);
        editor.putInt(KEY_ID_USER, user.getId());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putInt(KEY_GENDER, user.getGender());
        editor.putString(KEY_DEVICE_ID, user.getDeviceId());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putString(KEY_BIO, user.getBio());
        editor.putString(KEY_PHOTO_URL, user.getPhotoUrl());
        editor.putInt(KEY_IS_STUDENT, user.getIsStudent());
        editor.putInt(KEY_IS_TUTOR, user.getIsTutor());
        editor.putInt(KEY_IS_PROFILE_COMPLETE, user.getIsProfileComplete());
        editor.putString(KEY_EDUCATIONS, new Gson().toJson(user.getEducations()));
        editor.putInt(KEY_MAX_TRAVEL_DISTANCE, user.getMaxTravelDistance());
        editor.putInt(KEY_MIN_PRICE_RATE, user.getMinPriceRate());
        editor.putString(KEY_SUBJECTS, new Gson().toJson(user.getSubjects()));
        editor.putInt(KEY_IS_AVAILABLE, user.getIsAvailable());
        editor.putString(KEY_TIMESLOTS, new Gson().toJson(user.getTimeslots()));
        editor.putString(KEY_AVAILABILITIES, new Gson().toJson(user.getAvailabilities()));
        editor.putString(KEY_LATITUDE, user.getLatitude());
        editor.putString(KEY_LONGITUDE, user.getLongitude());
        editor.putString(KEY_LOCATION_ADDRESS, user.getLocationAddress());
        // commit changes
        editor.commit();
    }

    public User getUserDetail(){
        User user = new User();
        user.setId(pref.getInt(KEY_ID_USER, 0));
        user.setName(pref.getString(KEY_NAME, null));
        user.setEmail(pref.getString(KEY_EMAIL, null));
        user.setGender(pref.getInt(KEY_GENDER, 0));
        user.setDeviceId(pref.getString(KEY_DEVICE_ID, null));
        user.setPhone(pref.getString(KEY_PHONE, null));
        user.setPhotoUrl(pref.getString(KEY_PHOTO_URL, null));
        user.setIsTutor(pref.getInt(KEY_IS_TUTOR, 0));
        user.setIsStudent(pref.getInt(KEY_IS_STUDENT, 0));
        user.setIsProfileComplete(pref.getInt(KEY_IS_PROFILE_COMPLETE, 0));
        user.setBio(pref.getString(KEY_BIO, null));
        user.setMaxTravelDistance(pref.getInt(KEY_MAX_TRAVEL_DISTANCE, App.SETTING_TRAVEL_DISTANCE_MAX_DEFAULT));
        user.setMinPriceRate(pref.getInt(KEY_MIN_PRICE_RATE, 0));
        user.setLatitude(pref.getString(KEY_LATITUDE, null));
        user.setLongitude(pref.getString(KEY_LONGITUDE, null));
        user.setLocationAddress(pref.getString(KEY_LOCATION_ADDRESS, null));
        user.setIsAvailable(pref.getInt(KEY_IS_AVAILABLE, 0));
        Type eduType = new TypeToken<List<Education>>(){}.getType();
        List<Education> educationList = new Gson().fromJson(pref.getString(KEY_EDUCATIONS, null), eduType);
        if(educationList == null)
            user.setEducations(new ArrayList<Education>());
        else
            user.setEducations(educationList);

        Type subjectType = new TypeToken<Map<String, SavedSubject>>(){}.getType();
        Map<String, SavedSubject> subjectMap = new Gson().fromJson(pref.getString(KEY_SUBJECTS, null), subjectType);
        if(subjectMap != null) {
            // cast process
            user.setSubjects(new HashMap<String, SavedSubject>(subjectMap));
        }

        Type timeslotType = new TypeToken<Map<String, List<Integer>>>(){}.getType();
        Map<String, List<Integer>> timeslotMap = new Gson().fromJson(pref.getString(KEY_TIMESLOTS, null), timeslotType);
        if(timeslotMap != null) {
            // cast process
            user.setTimeslots(new HashMap<String, List<Integer>>(timeslotMap));
        }

        Type availabilityType = new TypeToken<Map<String, List<AvailabilityPerDay>>>(){}.getType();
        Map<String, List<AvailabilityPerDay>> availabilityMap = new Gson().fromJson(pref.getString(KEY_AVAILABILITIES, null), availabilityType);
        if(availabilityMap != null) {
            // cast process
            user.setAvailabilities(new HashMap<String, List<AvailabilityPerDay>>(availabilityMap));
        }
        return user;
    }

    public void logout(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }
}
