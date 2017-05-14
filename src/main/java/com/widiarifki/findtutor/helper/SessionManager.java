package com.widiarifki.findtutor.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.widiarifki.findtutor.model.User;

/**
 * Created by widiarifki on 12/05/2017.
 */

public class SessionManager {
    private static String TAG = SessionManager.class.getSimpleName();
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
    private static final String IS_LOGIN = "isLoggedIn";

    public static final String KEY_ID_USER = "id_user";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_IS_TUTOR = "is_tutor";
    public static final String KEY_IS_STUDENT = "is_student";
    public static final String KEY_IS_PROFILE_COMPLETE = "is_profile_complete";

    // Constructor
    public SessionManager(Context context){
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void createSession(User user){
        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_ID_USER, user.getId());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putInt(KEY_IS_STUDENT, user.getIsStudent());
        editor.putInt(KEY_IS_TUTOR, user.getIsTutor());
        editor.putInt(KEY_IS_PROFILE_COMPLETE, user.getIsProfileComplete());

        // commit changes
        editor.commit();
    }

    public void updateSession(User user){
        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_ID_USER, user.getId());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putInt(KEY_IS_STUDENT, user.getIsStudent());
        editor.putInt(KEY_IS_TUTOR, user.getIsTutor());
        editor.putInt(KEY_IS_PROFILE_COMPLETE, user.getIsProfileComplete());

        // commit changes
        editor.commit();
    }

    public void logout(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    public User getUserDetail(){
        User user = new User();
        user.setId(pref.getInt(KEY_ID_USER, 0));
        user.setName(pref.getString(KEY_NAME, null));
        user.setEmail(pref.getString(KEY_EMAIL, null));
        user.setPhone(pref.getString(KEY_PHONE, null));
        user.setIsTutor(pref.getInt(KEY_IS_TUTOR, 0));
        user.setIsStudent(pref.getInt(KEY_IS_STUDENT, 0));
        user.setIsProfileComplete(pref.getInt(KEY_IS_PROFILE_COMPLETE, 0));

        return user;
    }
}
