package com.widiarifki.findtutor.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.widiarifki.findtutor.CompleteProfileActivity;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.model.User;

import org.joda.time.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by widiarifki on 11/05/2017.
 */

public class App {

    public static String URL_REGISTER = "http://app-widiarifki.rhcloud.com/api/post-user-register";
    public static String URL_LOGIN = "http://app-widiarifki.rhcloud.com/api/post-user-login";
    public static String URL_SAVE_PROFILE = "http://app-widiarifki.rhcloud.com/api/post-user-profile";
    public static String URL_EDIT_PROFILE = "http://app-widiarifki.rhcloud.com/api/post-edit-profile";
    public static String URL_EDIT_TUTOR_PREF = "http://app-widiarifki.rhcloud.com/api/post-edit-tutor-pref";
    public static String URL_SET_AVAILABILITY = "http://app-widiarifki.rhcloud.com/api/post-set-availability";
    public static String URL_SAVE_TUTOR_TIMESLOT = "http://app-widiarifki.rhcloud.com/api/post-tutor-timeslot";
    public static String URL_ADD_AVAILABILITY = "http://app-widiarifki.rhcloud.com/api/post-add-availability";
    public static String URL_RMV_AVAILABILITY = "http://app-widiarifki.rhcloud.com/api/remove-availability";
    public static String URL_GET_SCHOOL_LEVEL = "http://app-widiarifki.rhcloud.com/api/get-list-school-level";
    public static String URL_GET_SUBJECT_LIST = "http://app-widiarifki.rhcloud.com/api/get-list-subject";
    public static String URL_REQUEST_TUTOR = "http://app-widiarifki.rhcloud.com/api/post-request-tutor";
    public static String URL_GET_TUTOR_INFO = "http://app-widiarifki.rhcloud.com/api/get-tutor-info";
    public static String URL_GET_TUTOR_TIMESLOT = "http://app-widiarifki.rhcloud.com/api/get-tutor-timeslot";
    public static String URL_GET_TUTOR_TIME = "http://app-widiarifki.rhcloud.com/api/get-tutor-available-time";
    public static String URL_BOOK_TUTOR = "http://app-widiarifki.rhcloud.com/api/post-book-tutor";
    public static String URL_GET_SESSION = "http://app-widiarifki.rhcloud.com/api/get-session";
    public static String URL_GET_SESSION_PENDING = "http://app-widiarifki.rhcloud.com/api/get-session-pending";
    public static String URL_GET_SESSION_ACCEPTED = "http://app-widiarifki.rhcloud.com/api/get-session-accepted";
    public static String URL_POST_SESSION_APPROVAL = "http://app-widiarifki.rhcloud.com/api/post-session-approval";
    public static String URL_POST_SESSION_APPROVAL_REGULAR = "http://app-widiarifki.rhcloud.com/api/post-session-approval-reg";
    public static String URL_POST_SESSION_START = "http://app-widiarifki.rhcloud.com/api/post-session-start";
    public static String URL_POST_SESSION_END = "http://app-widiarifki.rhcloud.com/api/post-session-end";
    public static String URL_POST_STUDENT_FEEDBACK = "http://app-widiarifki.rhcloud.com/api/post-student-feedback";
    public static String URL_GET_SESSION_OVERVIEW = "http://app-widiarifki.rhcloud.com/api/get-session-overview";
    public static String URL_POST_USER_COMPLAIN = "http://app-widiarifki.rhcloud.com/api/post-user-complain";
    public static String URL_POST_TUTOR_FEEDBACK = "http://app-widiarifki.rhcloud.com/api/post-tutor-feedback";
    public static String URL_GET_USER_COMPLAIN = "http://app-widiarifki.rhcloud.com/api/get-user-complain";
    public static String URL_POST_SESSION_CANCEL = "http://app-widiarifki.rhcloud.com/api/post-session-cancel";
    public static String URL_POST_BOOK_REGULAR = "http://app-widiarifki.rhcloud.com/api/book-regular";

    public static String URL_PATH_PHOTO = "http://app-widiarifki.rhcloud.com/uploads/user/photo/";

    public static Class<?> HOME_ACTIVITY = MainActivity.class;
    public static Class<?> COMPLETE_PROFILE_ACTIVITY = CompleteProfileActivity.class;

    public static int SEEKBAR_TRAVEL_DISTANCE_MIN = 1;
    public static int SEEKBAR_TRAVEL_DISTANCE_MAX = 50;
    public static int SEEKBAR_PRICE_RATE_MAX = 100000;
    public static int SEEKBAR_PRICE_RATE_RANGE = 5000;
    public static int SETTING_TRAVEL_DISTANCE_MAX_DEFAULT = 1;
    public static int SETTING_MIN_PASSWORD_LEN = 5;

    public static List<Location> detectedDeviceLocations;
    public static Location detectedDeviceLocation;

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

    public static void showSimpleDialog(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showSimpleDialog(Context context, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null);
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

    public static void hideSoftKeyboard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(((AppCompatActivity) context).getCurrentFocus() != null)
            inputManager.hideSoftInputFromWindow(((AppCompatActivity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static String convertDateFormat(String formatX, String formatY, String dateStr){
        SimpleDateFormat fromFormat = new SimpleDateFormat(formatX);
        fromFormat.setLenient(false);
        SimpleDateFormat toFormat = new SimpleDateFormat(formatY);
        toFormat.setLenient(false);
        Date date = null;
        try {
            date = fromFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String returnStr = toFormat.format(date);
        if(formatY.indexOf("EEE") >= 0){
            LocalDate localDate = new LocalDate(date);
            String dayInIna = Constants.DAY_INA[localDate.getDayOfWeek() - 1];
            returnStr = returnStr.replace(returnStr.substring(0,3), dayInIna);
        }
        return returnStr;
    }
}
