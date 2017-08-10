package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.SessionPreviousAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.model.Session;
import com.widiarifki.findtutor.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by widiarifki on 09/07/2017.
 */

public class SessionPreviousFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    Fragment mThis;
    private Context mContext;
    private Activity mContextActivity;
    private int mContextUser;
    private SessionManager mSession;
    private User mUserLogin;
    Call mHttpCall;

    ProgressBar mProgressBar;
    TextView mEmptyText;
    RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRvSwipeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mContextActivity = (Activity)mContext;
        mSession = new SessionManager(mContext);
        mUserLogin = mSession.getUserDetail();
        mThis = this;

        View view = inflater.inflate(R.layout.fragment_session_previous, container, false);

        TabLayout topTabs = (TabLayout) view.findViewById(R.id.topTabs);
        if(mUserLogin.getIsStudent() == 1 && mUserLogin.getIsTutor() == 1) {
            topTabs.addTab(topTabs.newTab().setText("Sebagai Tutor"));
            topTabs.addTab(topTabs.newTab().setText("Sebagai Siswa"));
            topTabs.setOnTabSelectedListener(this);
        }else{
            topTabs.setVisibility(View.GONE);
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mEmptyText = (TextView) view.findViewById(R.id.emptyText);

        if(mUserLogin.getIsStudent() == 1 && mUserLogin.getIsTutor() == 1) {
            mContextUser = Constants.SESSION_CONTEXT_AS_TUTOR;
        }
        else if (mUserLogin.getIsTutor() == 1 && mUserLogin.getIsStudent() == 0){
            mContextUser = Constants.SESSION_CONTEXT_AS_TUTOR;
        }
        else if (mUserLogin.getIsStudent() == 1 && mUserLogin.getIsTutor() == 0){
            mContextUser = Constants.SESSION_CONTEXT_AS_STUDENT;
        }
        mRvSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.rvSwipeLayout);
        mRvSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData(mContextUser);
            }
        });
        fetchData(mContextUser);

        return view;
    }

    void fetchData(int contextUser){
        if(mProgressBar.getVisibility() == View.GONE) mProgressBar.setVisibility(View.VISIBLE);
        if(mEmptyText.getVisibility() == View.VISIBLE) mEmptyText.setVisibility(View.GONE);
        if(mRecyclerView.getVisibility() == View.VISIBLE) mRecyclerView.setVisibility(View.GONE);

        // If it has executed, cancel it
        if(mHttpCall != null && mHttpCall.isExecuted()){
            mHttpCall.cancel();
        }

        RequestBody formBody = new FormBody.Builder()
                .add(Constants.PARAM_KEY_ID_USER, mUserLogin.getId()+"")
                .add(Constants.PARAM_KEY_USER_CONTEXT, contextUser +"")
                .add(Constants.PARAM_KEY_SESSION_STATE, Constants.SESSION_FINISHED+"")
                .build();
        OkHttpClient httpClient = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_GET_SESSION)
                .post(formBody)
                .build();
        mHttpCall = httpClient.newCall(httpRequest);
        mHttpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful() && response.code() == 200){
                    String json = response.body().string();
                    try {
                        JSONArray jsonArr = new JSONArray(json);
                        if(jsonArr.length() > 0){
                            final List<Session> listSession = new ArrayList<Session>();
                            for (int i = 0; i < jsonArr.length(); i++){
                                JSONObject dataObj = jsonArr.getJSONObject(i);
                                Session dataSession = new Session();
                                dataSession.setId(dataObj.getInt("id_session"));
                                dataSession.setScheduleDate(dataObj.getString("schedule_date"));
                                dataSession.setStartHour(dataObj.getString("start_hour"));
                                dataSession.setEndHour(dataObj.getString("end_hour"));
                                dataSession.setIsScheduleAccepted(dataObj.getInt("is_accepted"));
                                dataSession.setStatus(dataObj.getInt("status"));
                                dataSession.setLatitude(dataObj.getString("latitude"));
                                dataSession.setLongitude(dataObj.getString("longitude"));
                                dataSession.setLocationAddress(dataObj.getString("location_address"));
                                dataSession.setDistanceBetween(Double.parseDouble(dataObj.getString("distance")));
                                dataSession.setSubject(dataObj.getString("subject_name"));
                                dataSession.setDateHeld(dataObj.getString("date_held"));
                                dataSession.setStartHourHeld(dataObj.getString("start_hour_held"));
                                dataSession.setEndHourHeld(dataObj.getString("end_hour_held"));
                                dataSession.setTutorFee(dataObj.getString("tutor_fee"));
                                User dataUser = new User();
                                dataUser.setName(dataObj.getString("name"));
                                dataUser.setPhotoUrl(dataObj.getString("photo_url"));
                                dataSession.setUser(dataUser);
                                listSession.add(dataSession);
                            }

                            mContextActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(mProgressBar.getVisibility() == View.VISIBLE) mProgressBar.setVisibility(View.GONE);
                                    if(mEmptyText.getVisibility() == View.VISIBLE) mEmptyText.setVisibility(View.GONE);
                                    if(mRecyclerView.getVisibility() == View.GONE) mRecyclerView.setVisibility(View.VISIBLE);

                                    SessionPreviousAdapter adapter = new SessionPreviousAdapter(mContext, listSession, mThis, mContextUser);
                                    if(mRecyclerView.getAdapter() == null){
                                        mRecyclerView.setAdapter(adapter);
                                        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                                    }else{
                                        mRecyclerView.swapAdapter(adapter, false);
                                    }

                                    if(mRvSwipeLayout.isRefreshing()){
                                        mRvSwipeLayout.setRefreshing(false);
                                    };
                                }
                            });
                        }else{
                            mContextActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(mProgressBar.getVisibility() == View.VISIBLE) mProgressBar.setVisibility(View.GONE);
                                    if(mEmptyText.getVisibility() == View.GONE) mEmptyText.setVisibility(View.VISIBLE);
                                    if(mRecyclerView.getVisibility() == View.VISIBLE) mRecyclerView.setVisibility(View.GONE);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{

                }
            }
        });
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(tab.getPosition() == 0){
            fetchData(Constants.SESSION_CONTEXT_AS_TUTOR);
        }
        else if(tab.getPosition() == 1){
            fetchData(Constants.SESSION_CONTEXT_AS_STUDENT);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
