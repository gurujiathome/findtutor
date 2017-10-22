package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.SubjectTutorSettingAdapter;
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

public class SettingSelectSubjectFragment extends Fragment {

    private Context mContext;
    private Activity mContextActivity;
    private MainActivity mParentActivity;
    ArrayList<Subject> mSubjects;

    EditText mSearchInput;
    private ListView mListViewSubject;
    ProgressDialog mProgressDialog;
    private SubjectTutorSettingAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        mContextActivity = (Activity)mContext;
        mParentActivity = (MainActivity)mContext;
        mSubjects = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_setting_select_subject, container, false);
        mListViewSubject = (ListView) view.findViewById(R.id.lvSubject);
        mSearchInput = (EditText) view.findViewById(R.id.searchInput);
        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                mAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);

        fetchRefData();

        return view;
    }

    private void fetchRefData() {
        mProgressDialog.setMessage("Tunggu sebentar...");
        if(!mProgressDialog.isShowing()) mProgressDialog.show();

        OkHttpClient client = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_GET_SUBJECT_LIST)
                .build();
        Call httpCall = client.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

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
                                mSubjects.add(new SubjectTopic(idCategory, id, name));
                            }else{
                                mSubjects.add(new SubjectTopic(idCategory, id, name));
                            }
                        }
                        /** Hide progress bae **/
                        mContextActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter = new SubjectTutorSettingAdapter(mContext, mSubjects);
                                mListViewSubject.setAdapter(mAdapter);
                                if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        App.hideSoftKeyboard(mContext);
    }
}
