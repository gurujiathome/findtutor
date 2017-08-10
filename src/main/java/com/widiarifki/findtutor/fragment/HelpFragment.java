package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.HelpAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.model.Complain;
import com.widiarifki.findtutor.model.Session;
import com.widiarifki.findtutor.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by widiarifki on 26/07/2017.
 */

public class HelpFragment extends Fragment {

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mContextActivity = (Activity)mContext;
        mSession = new SessionManager(mContext);
        mUserLogin = mSession.getUserDetail();
        mThis = this;

        View view = inflater.inflate(R.layout.fragment_help, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mEmptyText = (TextView) view.findViewById(R.id.emptyText);

        fetchData();

        return view;
    }

    void fetchData(){
        if(mProgressBar.getVisibility() == View.GONE) mProgressBar.setVisibility(View.VISIBLE);
        if(mEmptyText.getVisibility() == View.VISIBLE) mEmptyText.setVisibility(View.GONE);
        if(mRecyclerView.getVisibility() == View.VISIBLE) mRecyclerView.setVisibility(View.GONE);

        // If it has executed, cancel it
        if(mHttpCall != null && mHttpCall.isExecuted()){
            mHttpCall.cancel();
        }

        OkHttpClient httpClient = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_GET_USER_COMPLAIN + "?idUser=" + mUserLogin.getId())
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
                        JSONObject jsonObj = new JSONObject(json);
                        if(jsonObj.getInt("success") == 1){
                            try {
                                JSONArray jsonArr = jsonObj.getJSONArray("data");
                                if(jsonArr.length() > 0){
                                    final List<Complain> listComplain = new ArrayList<Complain>();
                                    for (int i = 0; i < jsonArr.length(); i++){
                                        JSONObject dataObj = jsonArr.getJSONObject(i);
                                        Complain dataComplain = new Complain();

                                        Session dataSession = new Session();
                                        dataSession.setId(dataObj.getInt("id_session"));
                                        User dataUser = new User();
                                        dataUser.setId(dataObj.getInt("id_user"));

                                        dataComplain.setUser(dataUser);
                                        dataComplain.setSession(dataSession);
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String createdTime = dataObj.getString("created_time");
                                        String updatedTime = dataObj.getString("updated_time");
                                        try {
                                            dataComplain.setCreatedTime(sdf.parse(createdTime));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            dataComplain.setLastUpdate(sdf.parse(updatedTime));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        dataComplain.setComplain(dataObj.getString("user_complain"));
                                        dataComplain.setReply(dataObj.getString("admin_reply"));

                                        listComplain.add(dataComplain);
                                    }

                                    mContextActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(mProgressBar.getVisibility() == View.VISIBLE) mProgressBar.setVisibility(View.GONE);
                                            if(mEmptyText.getVisibility() == View.VISIBLE) mEmptyText.setVisibility(View.GONE);
                                            if(mRecyclerView.getVisibility() == View.GONE) mRecyclerView.setVisibility(View.VISIBLE);

                                            HelpAdapter adapter = new HelpAdapter(mContext, listComplain, mThis);
                                            if(mRecyclerView.getAdapter() == null){
                                                mRecyclerView.setAdapter(adapter);
                                                mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                                            }else{
                                                mRecyclerView.swapAdapter(adapter, false);
                                            }
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else{

                }
            }
        });
    }
}
