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

    //public static String API_ROOT = "http://app-widiarifki.rhcloud.com/api/";
    //public static String API_ROOT = "http://widiarif.heliohost.org/web/api/";
    public static String API_ROOT = "http://18.220.128.11/findtutor/web/api/";
    public static String URL_REGISTER = API_ROOT +"post-user-register";
    public static String URL_LOGIN = API_ROOT +"post-user-login";
    public static String URL_SAVE_PROFILE = API_ROOT +"post-user-profile";
    public static String URL_EDIT_PROFILE = API_ROOT +"post-edit-profile";
    public static String URL_EDIT_TUTOR_PREF = API_ROOT +"post-edit-tutor-pref";
    public static String URL_SET_AVAILABILITY = API_ROOT +"post-set-availability";
    public static String URL_SAVE_TUTOR_TIMESLOT = API_ROOT +"post-tutor-timeslot";
    public static String URL_ADD_AVAILABILITY = API_ROOT +"post-add-availability";
    public static String URL_RMV_AVAILABILITY = API_ROOT +"remove-availability";
    public static String URL_GET_SCHOOL_LEVEL = API_ROOT +"get-list-school-level";
    public static String URL_GET_SUBJECT_LIST = API_ROOT +"get-list-subject";
    public static String URL_REQUEST_TUTOR = API_ROOT +"post-request-tutor";
    public static String URL_GET_TUTOR_INFO = API_ROOT +"get-tutor-info";
    public static String URL_GET_TUTOR_TIMESLOT = API_ROOT +"get-tutor-timeslot";
    public static String URL_GET_TUTOR_TIME = API_ROOT +"get-tutor-available-time";
    public static String URL_BOOK_TUTOR = API_ROOT +"post-book-tutor";
    public static String URL_GET_SESSION = API_ROOT +"get-session";
    public static String URL_GET_SESSION_PENDING = API_ROOT +"get-session-pending";
    public static String URL_GET_SESSION_ACCEPTED = API_ROOT +"get-session-accepted";
    public static String URL_POST_SESSION_APPROVAL = API_ROOT +"post-session-approval";
    public static String URL_POST_SESSION_APPROVAL_REGULAR = API_ROOT +"post-session-approval-reg";
    public static String URL_POST_SESSION_START = API_ROOT +"post-session-start";
    public static String URL_POST_SESSION_END = API_ROOT +"post-session-end";
    public static String URL_POST_STUDENT_FEEDBACK = API_ROOT +"post-student-feedback";
    public static String URL_GET_SESSION_OVERVIEW = API_ROOT +"get-session-overview";
    public static String URL_POST_USER_COMPLAIN = API_ROOT +"post-user-complain";
    public static String URL_POST_TUTOR_FEEDBACK = API_ROOT +"post-tutor-feedback";
    public static String URL_GET_USER_COMPLAIN = API_ROOT +"get-user-complain";
    public static String URL_POST_SESSION_CANCEL = API_ROOT +"post-session-cancel";
    public static String URL_POST_BOOK_REGULAR = API_ROOT +"book-regular";

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
    public static String urlUserUploadPhoto = API_ROOT +"upload-photo";
    public static String urlUserCompleteForm1 = API_ROOT +"user-identity-1";
    public static String urlUserCompleteForm2 = API_ROOT +"user-identity-2";

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
