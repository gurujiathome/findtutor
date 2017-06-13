package com.widiarifki.findtutor.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.widiarifki.findtutor.CompleteProfileActivity;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.helper.SessionManager;
import com.widiarifki.findtutor.model.User;

/**
 * Created by widiarifki on 11/05/2017.
 */

public class App {

    public static String URL_USER_REGISTER = "http://app-widiarifki.rhcloud.com/api/post-user-register";
    public static String URL_USER_LOGIN = "http://app-widiarifki.rhcloud.com/api/post-user-login";
    public static String URL_USER_PROFILE_FORM = "http://app-widiarifki.rhcloud.com/api/post-user-profile";
    public static String URL_EDIT_PROFILE = "http://app-widiarifki.rhcloud.com/api/post-edit-profile";
    public static String URL_EDIT_TUTOR_PREF = "http://app-widiarifki.rhcloud.com/api/post-edit-tutor-pref";
    public static String URL_ADD_AVAILABILITY = "http://app-widiarifki.rhcloud.com/api/post-add-availability";
    public static String URL_RMV_AVAILABILITY = "http://app-widiarifki.rhcloud.com/api/remove-availability";
    public static String URL_GET_SCHOOL_LEVEL = "http://app-widiarifki.rhcloud.com/api/get-list-school-level";
    public static String URL_GET_SUBJECT_LIST = "http://app-widiarifki.rhcloud.com/api/get-list-subject";

    public static String URL_PATH_PHOTO = "http://app-widiarifki.rhcloud.com/uploads/user/photo/";

    public static Class<?> HOME_ACTIVITY = MainActivity.class;
    public static Class<?> COMPLETE_PROFILE_ACTIVITY = CompleteProfileActivity.class;

    public static int MAX_TRAVEL_DISTANCE_DEFAULT = 1;
    public static int SEEKBAR_TRAVEL_DISTANCE_MIN = 1;
    public static int SEEKBAR_TRAVEL_DISTANCE_MAX = 50;
    public static int SEEKBAR_PRICE_RATE_MAX = 100000;
    public static int SEEKBAR_PRICE_RATE_RANGE = 5000;

    /** UNUSED **/
    public static String urlUserUploadPhoto = "http://app-widiarifki.rhcloud.com/api/upload-photo";
    public static String urlUserCompleteForm1 = "http://app-widiarifki.rhcloud.com/api/user-identity-1";
    public static String urlUserCompleteForm2 = "http://app-widiarifki.rhcloud.com/api/user-identity-2";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public static void showSimpleDialog(Context context, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void loadInitActivity(Context context){
        SessionManager session = new SessionManager(context);
        User userLogin = session.getUserDetail();
        Intent intent;

        if(session.isLoggedIn()){
            if(userLogin.getIsProfileComplete() == 1){
                intent = new Intent(context, App.HOME_ACTIVITY);
            }else{
                intent = new Intent(context, App.COMPLETE_PROFILE_ACTIVITY);
            }
            context.startActivity(intent);
            ((Activity)context).finish();
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

        listView.setSelection(listAdapter.getCount() - 1);
    }
}
