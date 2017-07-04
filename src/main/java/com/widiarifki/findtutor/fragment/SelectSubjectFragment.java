package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.SelectSubjectAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.model.Subject;
import com.widiarifki.findtutor.model.SubjectCategory;
import com.widiarifki.findtutor.model.SubjectTopic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by widiarifki on 04/06/2017.
 */

public class SelectSubjectFragment extends Fragment {

    private Context mContext;
    private Activity mContextActivity;
    ArrayList<Subject> mSubjects;

    private ListView mListViewSubject;
    ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        mContextActivity = (Activity)mContext;
        mSubjects = new ArrayList<Subject>();

        View view = inflater.inflate(R.layout.fragment_select_subject, container, false);

        mListViewSubject = (ListView) view.findViewById(R.id.lvSubject);
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);
        settleRefData();

        return view;
    }

    private void settleRefData() {
        mProgressDialog.setMessage("Tunggu sebentar...");
        if(!mProgressDialog.isShowing()) mProgressDialog.show();

        OkHttpClient client = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_GET_SUBJECT_LIST)
                .build();
        Call httpCall = client.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                if(response.isSuccessful() && response.code() == 200){
                    try {
                        JSONArray dataJson = new JSONArray(json);
                        for (int i = 0; i < dataJson.length(); i++) {
                            JSONObject dataObj = dataJson.getJSONObject(i);
                            int idCategory = dataObj.getInt("FK_ID_PARENT");
                            int id = dataObj.getInt("ID_REF_SUBJECT");
                            String name = dataObj.getString("SUBJECT_NAME");
                            if(idCategory == 0){
                                mSubjects.add(new SubjectCategory(id, name));
                                mSubjects.add(new SubjectTopic(idCategory, id, "Semua topik di " + name));
                            }else{
                                mSubjects.add(new SubjectTopic(idCategory, id, name));
                            }
                        }
                        /** Hide progress bae **/
                        mContextActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SelectSubjectAdapter adapter = new SelectSubjectAdapter(mContext, mSubjects);
                                mListViewSubject.setAdapter(adapter);
                                if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
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
