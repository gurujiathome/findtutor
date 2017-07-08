package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.SearchTutorResultAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.SubjectTopic;
import com.widiarifki.findtutor.model.User;

import org.joda.time.LocalDate;
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
 * Created by widiarifki on 30/06/2017.
 */

public class SearchTutorResultFragment extends Fragment {

    private Context mContext;
    Activity mContextActivity;
    SessionManager mSession;
    MainActivity mParentActivity;
    User mUser;
    Fragment mThisFragment;
    Bundle mParams;

    RecyclerView rvTutorList;
    String mRequestedDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mThisFragment = this;
        //mContext = container.getContext();
        mContext = getContext();
        mContextActivity = (Activity)mContext;
        mParentActivity = ((MainActivity)mContext);
        mSession = new SessionManager(mContext);
        mUser = mSession.getUserDetail();
        mParams = getArguments();

        View view = inflater.inflate(R.layout.fragment_search_tutor_result, container, false);
        rvTutorList = (RecyclerView) view.findViewById(R.id.rvTutorList);

        fetchResult();
        return view;
    }

    private void fetchResult() {
        Location location = mParentActivity.mSearchTutorLocation;
        final LocalDate date = mParentActivity.mSearchTutorDate;
        HashMap<String, SavedSubject> subjects = mParentActivity.getSearchTutorSubject();
        List<Integer> subjectId = new ArrayList<Integer>();
        for (SavedSubject group : subjects.values()){
            int idParent = group.getCategoryId();
            String parentName = group.getCategoryName();
            HashMap<String, SubjectTopic> topic = group.getTopicList();
            if(topic.containsKey(idParent+"") && !subjectId.contains(idParent)){
                subjectId.add(idParent);
            }else{
                for (SubjectTopic topicItem : topic.values())
                    subjectId.add(topicItem.getId());
            }
        }

        String jsonSubjectList = new Gson().toJson(subjectId);
        RequestBody formBody = new FormBody.Builder()
                .add(Constants.PARAM_KEY_ID_USER, mUser.getId()+"")
                .add("subjects", jsonSubjectList)
                .add(Constants.PARAM_KEY_SCHEDULE_DATE, date.toString())
                .add(Constants.PARAM_KEY_LATITUDE, location.getLatitude()+"")
                .add(Constants.PARAM_KEY_LONGITUDE, location.getLongitude()+"")
                .add(Constants.PARAM_KEY_GENDER, mParams.getInt(Constants.PARAM_KEY_GENDER, 0)+"")
                .build();

        OkHttpClient httpClient = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_REQUEST_TUTOR)
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
                    final List<User> tutors = new ArrayList<User>();
                    try {
                        JSONArray jsonArray = new JSONArray(json);
                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject data = jsonArray.getJSONObject(i);
                            User newUser = new User();
                            newUser.setId(data.getInt(Constants.PARAM_KEY_ID_USER));
                            newUser.setPhotoUrl(data.getString(Constants.PARAM_KEY_PHOTO_URL));
                            newUser.setName(data.getString(Constants.PARAM_KEY_NAME));
                            newUser.setDistanceFromRequestor(data.getDouble(Constants.PARAM_KEY_DISTANCE));
                            newUser.setLatitude(data.getString(Constants.PARAM_KEY_LATITUDE));
                            newUser.setLongitude(data.getString(Constants.PARAM_KEY_LONGITUDE));
                            newUser.setLocationAddress(data.getString(Constants.PARAM_KEY_LOCATION_ADDRESS));
                            tutors.add(newUser);
                        }

                        mContextActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /** Bind search result to list **/
                                SearchTutorResultAdapter adapter = new SearchTutorResultAdapter(mContext, tutors, mThisFragment);
                                rvTutorList.setAdapter(adapter);
                                rvTutorList.setLayoutManager(new LinearLayoutManager(mContext));
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{

                }
            }
        });
    }
}
