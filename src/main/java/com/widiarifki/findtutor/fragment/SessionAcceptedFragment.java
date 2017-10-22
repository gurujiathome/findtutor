package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.widiarifki.findtutor.adapter.SessionAcceptedAdapter;
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

public class SessionAcceptedFragment extends Fragment {

    private Context mContext;
    private Activity mContextActivity;
    private int mContextUser;
    private SessionManager mSession;
    private User mUserLogin;

    ProgressBar mProgressBar;
    TextView mEmptyText;
    RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRvSwipeLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle params = getArguments();
        mContextUser = params.getInt(Constants.PARAM_KEY_USER_CONTEXT, 1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mContextActivity = (Activity)mContext;
        mSession = new SessionManager(mContext);
        mUserLogin = mSession.getUserDetail();

        View view = inflater.inflate(R.layout.fragment_session_accepted, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRvSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.rvSwipeLayout);
        mRvSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mEmptyText = (TextView) view.findViewById(R.id.emptyText);

        fetchData();

        return view;
    }

    void fetchData(){
        if(mProgressBar.getVisibility() == View.GONE) mProgressBar.setVisibility(View.VISIBLE);
        if(mEmptyText.getVisibility() == View.VISIBLE) mEmptyText.setVisibility(View.GONE);
        if(mRecyclerView.getVisibility() == View.VISIBLE) mRecyclerView.setVisibility(View.GONE);

        RequestBody formBody = new FormBody.Builder()
                .add(Constants.PARAM_KEY_ID_USER, mUserLogin.getId()+"")
                .add(Constants.PARAM_KEY_USER_CONTEXT, mContextUser +"")
                .add(Constants.PARAM_KEY_SESSION_STATE, Constants.SESSION_ACCEPTED+"")
                .build();
        OkHttpClient httpClient = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_GET_SESSION_ACCEPTED)
                .post(formBody)
                .build();
        Call httpCall = httpClient.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
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
                                //dataSession.setIsScheduleAccepted(dataObj.getInt("is_accepted"));
                                dataSession.setStatus(dataObj.getInt("status"));
                                dataSession.setLatitude(dataObj.getString("latitude"));
                                dataSession.setLongitude(dataObj.getString("longitude"));
                                dataSession.setLocationAddress(dataObj.getString("location_address"));
                                dataSession.setDistanceBetween(Double.parseDouble(dataObj.getString("distance")));
                                dataSession.setSubject(dataObj.getString("subject_name"));

                                User dataUser = new User();
                                dataUser.setName(dataObj.getString("name"));
                                dataUser.setPhotoUrl(dataObj.getString("photo_url"));
                                dataSession.setUser(dataUser);

                                dataSession.setDateHeld(dataObj.getString("date_held"));
                                dataSession.setStartHourHeld(dataObj.getString("start_hour_held"));

                                listSession.add(dataSession);
                            }

                            mContextActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(mProgressBar.getVisibility() == View.VISIBLE) mProgressBar.setVisibility(View.GONE);
                                    if(mEmptyText.getVisibility() == View.VISIBLE) mEmptyText.setVisibility(View.GONE);
                                    if(mRecyclerView.getVisibility() == View.GONE) mRecyclerView.setVisibility(View.VISIBLE);

                                    SessionAcceptedAdapter adapter = new SessionAcceptedAdapter(mContext, listSession, mContextUser);
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
                }
            }
        });
    }
}
