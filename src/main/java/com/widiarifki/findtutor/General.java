package com.widiarifki.findtutor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.widiarifki.findtutor.helper.SessionManager;
import com.widiarifki.findtutor.model.User;

/**
 * Created by widiarifki on 11/05/2017.
 */

public class General {

    public static String urlUserRegister = "http://app-widiarifki.rhcloud.com/api/post-user-register";
    public static String urlUserLogin = "http://app-widiarifki.rhcloud.com/api/post-user-login";
    public static String urlUserCompleteForm = "http://app-widiarifki.rhcloud.com/api/post-user-profile";

    public static String urlUserUploadPhoto = "http://app-widiarifki.rhcloud.com/api/upload-photo";
    public static String urlUserCompleteForm1 = "http://app-widiarifki.rhcloud.com/api/user-identity-1";
    public static String urlUserCompleteForm2 = "http://app-widiarifki.rhcloud.com/api/user-identity-2";

    public static Class<?> homeActivity = MainActivity.class;
    public static Class<?> welcomeActivity = WelcomeActivity.class;
    public static Class<?> profileActivity = ProfileFormActivity.class;
    public static String PhotoFolderName = "FindTutor";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public static void showErrorDialog(Context context, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void initStartIntent(Context context){
        SessionManager session = new SessionManager(context.getApplicationContext());

        if (session.isLoggedIn()) {
            User userLogin = session.getUserDetail();
            Intent intent;
            // User is already logged in. Take him to main activity or complete profile
            if(userLogin.getIsProfileComplete() == 1){
                intent = new Intent(context, General.homeActivity);
            }else{
                intent = new Intent(context, General.profileActivity);
            }
            context.startActivity(intent);
            ((Activity) context).finish();
        }
    }
}
