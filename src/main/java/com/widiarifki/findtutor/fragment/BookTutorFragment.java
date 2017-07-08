package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.gson.Gson;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.SelectHourAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.SubjectTopic;
import com.widiarifki.findtutor.model.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by widiarifki on 06/07/2017.
 */

public class BookTutorFragment extends Fragment {

    Fragment mThis;
    Context mContext;
    Activity mContextActivity;
    MainActivity mParentActivity;
    private SessionManager mSession;
    private User mUserLogin;
    Call mHttpCall;

    TextView mTextDate;
    RecyclerView mHourPicker;

    int mIdStudent;
    int mIdTutor;
    HashMap<String, SavedSubject> mSubjects;
    String mStudentLatitude;
    String mStudentLongitude;
    String mSelectedDate;
    int mStartTime;
    int mEndTime;
    static List<Integer> mSelectedTimes;
    String mNote;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mThis = this;

        Bundle givenParams = getArguments();
        mIdTutor = givenParams.getInt(Constants.PARAM_KEY_ID_USER, 0);

        mSelectedTimes = new ArrayList<Integer>();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.appbar_menu, menu);
        menu.findItem(R.id.action_save).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            attemptBooking();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mContextActivity = ((Activity)mContext);
        mParentActivity = (MainActivity) mContext;
        mSession = new SessionManager(mContext);

        mUserLogin = mSession.getUserDetail();
        mIdStudent = mUserLogin.getId();

        mStudentLatitude = "";
        mStudentLongitude = "";
        mSelectedDate = "";
        mSubjects = new HashMap<String, SavedSubject>();
        if(mParentActivity.mSearchTutorLocation != null){
            Location studentLocation = mParentActivity.mSearchTutorLocation;
            mStudentLatitude = String.valueOf(studentLocation.getLatitude());
            mStudentLongitude = String.valueOf(studentLocation.getLongitude());
        }
        if(mParentActivity.mSearchTutorDate != null){
            mSelectedDate = mParentActivity.mSearchTutorDate.toString();
        }
        if(mParentActivity.getSearchTutorSubject() != null){
            mSubjects = mParentActivity.getSearchTutorSubject();
        }

        View view = inflater.inflate(R.layout.fragment_book_tutor, container, false);

        //mTextDate = (TextView) view.findViewById(R.id.tvDate);
        HorizontalPicker datePicker = (HorizontalPicker) view.findViewById(R.id.datePicker);
        // Dont call at first time
        datePicker.setListener(new DatePickerListener() {
            @Override
            public void onDateSelected(DateTime dateSelected) {
                //String dateStr = DateTimeFormat.forPattern("dd MM yyyy").print(dateSelected);
                //mTextDate.setText(dateStr);
                mSelectedDate = DateTimeFormat.forPattern("yyyy-MM-dd").print(dateSelected);;
                getAvailableTime();
            }
        });
        datePicker.init();
        if(mSelectedDate != null) {
            datePicker.setDate(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(mSelectedDate));
            //mTextDate.setText(DateTimeFormat.forPattern("dd MM yyyy").print(date));
        }

        mHourPicker = (RecyclerView) view.findViewById(R.id.rvHourPicker);
        //getAvailableTime();

        return view;
    }

    private void getAvailableTime() {
        if(mHttpCall != null && mHttpCall.isExecuted()){
            mHttpCall.cancel();
        }

        OkHttpClient httpClient = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_GET_TUTOR_TIME + "?idUser=" + mIdTutor + "&date=" + mSelectedDate)
                .build();
        mHttpCall = httpClient.newCall(httpRequest);
        mHttpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code() == 200 && response.isSuccessful()){
                    String json = response.body().string();
                    try {
                        JSONObject jsonObj = new JSONObject(json);
                        if(jsonObj.getInt("success") == 1){
                            final int availabilityStatus = jsonObj.getInt("is_available");
                            final Integer tutorTimes[];
                            if(availabilityStatus == Constants.TUTOR_AVAILABLE_BY_SCHEDULE){
                                JSONArray times = jsonObj.getJSONArray(Constants.PARAM_KEY_TIMESLOTS);
                                int timeLn = times.length();
                                tutorTimes = new Integer[timeLn];
                                for(int i = 0; i < timeLn; i++){
                                    tutorTimes[i] = times.getInt(i);
                                }
                            }else{
                                JSONArray times = jsonObj.getJSONArray(Constants.PARAM_KEY_BOOKED_TIME);
                                int timeLn = times.length();
                                tutorTimes = new Integer[timeLn];
                                for(int i = 0; i < timeLn; i++){
                                    tutorTimes[i] = times.getInt(i);
                                }
                            }

                            mContextActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SelectHourAdapter adapter = new SelectHourAdapter(mContext, mThis, tutorTimes, availabilityStatus);
                                    if(mHourPicker.getAdapter() == null) {
                                        mHourPicker.setAdapter(adapter);
                                        mHourPicker.setLayoutManager(new GridLayoutManager(mContext, 3));
                                    }else{
                                        mHourPicker.swapAdapter(adapter, false);
                                    }
                                }
                            });
                        }else{

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{

                }
            }
        });
    }

    public int getStartTime() {
        return mStartTime;
    }

    public void setStartTime(int startTime) {
        mStartTime = startTime;
    }

    public int getEndTime() {
        return mEndTime;
    }

    public void setEndTime(int endTime) {
        mEndTime = endTime;
    }

    public static void addTime(int timeId){
        if(!mSelectedTimes.contains(timeId))
            mSelectedTimes.add(timeId);
    }

    public static void removeTime(int timeId){
        int value = Integer.valueOf(timeId);
        if(mSelectedTimes.contains(value))
            mSelectedTimes.remove(value);
    }

    private void attemptBooking() {
        List<Integer> subjectId = new ArrayList<Integer>();
        if(mSubjects != null){
            for (SavedSubject group : mSubjects.values()){
                int idParent = group.getCategoryId();
                HashMap<String, SubjectTopic> topic = group.getTopicList();
                if(topic.containsKey(idParent+"") && !subjectId.contains(idParent)){
                    subjectId.add(idParent);
                }else{
                    for (SubjectTopic topicItem : topic.values())
                        subjectId.add(topicItem.getId());
                }
            }
        }

        int startTime = mSelectedTimes.get(0);
        int endTime = mSelectedTimes.get(mSelectedTimes.size() - 1);
        int timeLn = endTime + 1 - startTime;
        String jsonSubjectList = new Gson().toJson(subjectId);
        RequestBody formBody = new FormBody.Builder()
                .add(Constants.PARAM_KEY_ID_REQUESTOR, mIdStudent+"")
                .add(Constants.PARAM_KEY_ID_TUTOR, mIdTutor+"")
                .add(Constants.PARAM_KEY_SUBJECTS, jsonSubjectList)
                .add(Constants.PARAM_KEY_LATITUDE, mStudentLatitude+"")
                .add(Constants.PARAM_KEY_LONGITUDE, mStudentLongitude+"")
                .add(Constants.PARAM_KEY_SCHEDULE_DATE, mSelectedDate)
                .add(Constants.PARAM_KEY_START_TIME_ID, startTime+"")
                .add(Constants.PARAM_KEY_TIME_LENGTH, timeLn+"")
                .build();

        OkHttpClient httpClient = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_BOOK_TUTOR)
                .post(formBody)
                .build();

        Call httpCall = httpClient.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200 && response.isSuccessful()){
                    String json = response.body().string();
                    Log.v("W", json);
                }else{
                    Log.v("W", response.message());
                }
            }
        });
    }
}
