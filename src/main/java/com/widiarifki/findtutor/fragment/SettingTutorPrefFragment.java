package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.SubjectListAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.helper.DialogMessage;
import com.widiarifki.findtutor.helper.SessionManager;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.SubjectTopic;
import com.widiarifki.findtutor.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by widiarifki on 05/06/2017.
 */

public class SettingTutorPrefFragment extends Fragment {

    private Context mContext;
    private Activity mContextActivity;
    private String dialogTitle = "Submit Data Gagal";

    SessionManager mSession;
    User mUserLogin;

    // UI comp.
    LinearLayout mFormLayout;
    SeekBar mSeekBarMaxDistance;
    SeekBar mSeekBarMinPrice;
    ListView mListViewSubject;
    Button mBtnChooseSubject;

    ProgressDialog mProgressDialog;

    HashMap<String, SavedSubject> mSavedSubject;
    SubjectListAdapter mSubjectListAdapter;
    ArrayList<String> mSavedSubjectTopic = new ArrayList<String>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveChanges();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        final View view = inflater.inflate(R.layout.fragment_setting_tutor_pref, container, false);

        mSession = new SessionManager(mContext);
        mUserLogin = mSession.getUserDetail();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);

        // Bind UI comp.
        mFormLayout = (LinearLayout) view.findViewById(R.id.form_layout);
        mSeekBarMaxDistance = (SeekBar) view.findViewById(R.id.seekbar_max_distance);
        mSeekBarMaxDistance.setMax(App.SEEKBAR_TRAVEL_DISTANCE_MAX);
        mSeekBarMaxDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress < App.SEEKBAR_TRAVEL_DISTANCE_MIN) progress = App.SEEKBAR_TRAVEL_DISTANCE_MIN;
                TextView preview = (TextView) view.findViewById(R.id.preview_max_distance);
                preview.setText("(" + progress + " KM)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekBarMinPrice = (SeekBar) view.findViewById(R.id.seekbar_min_price_rate);
        mSeekBarMinPrice.setMax(App.SEEKBAR_PRICE_RATE_MAX);
        mSeekBarMinPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / App.SEEKBAR_PRICE_RATE_RANGE;
                progress = progress * App.SEEKBAR_PRICE_RATE_RANGE;
                TextView preview = (TextView) view.findViewById(R.id.preview_min_price_rate);
                preview.setText("(Rp. " + progress + " / Jam)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mListViewSubject = (ListView) view.findViewById(R.id.list_subject);
        mBtnChooseSubject = (Button) view.findViewById(R.id.btn_choose_subject);
        mBtnChooseSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SelectSubjectFragment();
                ((MainActivity)mContext).addStackedFragment(fragment, getString(R.string.action_select_subject), getString(R.string.set_tutor_preference));
            }
        });

        /*mSavedSubject = ((MainActivity)getActivity()).getSelectedSubject();
        mSubjectListAdapter = new SubjectListAdapter(mContext, mSavedSubject);
        mListViewSubject = (ListView) view.findViewById(R.id.list_subject);
        mListViewSubject.setAdapter(mSubjectListAdapter);
        mSubjectListAdapter.notifyDataSetChanged();
        App.setListViewHeightBasedOnChildren(mListViewSubject);*/

        bindInitialData();

        return view;
    }

    void bindInitialData(){
        if(mUserLogin.getMaxTravelDistance() == 0)
            mSeekBarMaxDistance.setProgress(App.MAX_TRAVEL_DISTANCE_DEFAULT);
        else mSeekBarMaxDistance.setProgress(mUserLogin.getMaxTravelDistance());
        mSeekBarMinPrice.setProgress(mUserLogin.getMinPriceRate());
        /*if(mUserLogin.getSubjects()==null){
            mSavedSubject = ((MainActivity)getActivity()).getSelectedSubject();
        }else{
            mSavedSubject = mUserLogin.getSubjects();
        }*/
        mSavedSubject = ((MainActivity)getActivity()).getSelectedSubject();
        mSubjectListAdapter = new SubjectListAdapter(mContext, mSavedSubject);
        mListViewSubject.setAdapter(mSubjectListAdapter);
        mSubjectListAdapter.notifyDataSetChanged();
        App.setListViewHeightBasedOnChildren(mListViewSubject);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void saveChanges() {
        // Store values
        int maxDistance = mSeekBarMaxDistance.getProgress();
        if(maxDistance < App.SEEKBAR_TRAVEL_DISTANCE_MIN) maxDistance = App.SEEKBAR_TRAVEL_DISTANCE_MIN;
        int minPriceRawVal = mSeekBarMinPrice.getProgress();
        int minPrice = minPriceRawVal / App.SEEKBAR_PRICE_RATE_RANGE;
        minPrice = minPrice * App.SEEKBAR_PRICE_RATE_RANGE;
        if(mSavedSubject != null){
            mSavedSubjectTopic = new ArrayList<String>();
            for(SavedSubject object : mSavedSubject.values()){
                int idParent = object.getCategoryId();
                HashMap<String, SubjectTopic> topicList = object.getTopicList();
                if(topicList.get(idParent+"") == null){
                    /** If user not checked 'Semua' **/
                    for(SubjectTopic topic : topicList.values()){
                        mSavedSubjectTopic.add(topic.getId()+"");
                    }
                }else{
                    mSavedSubjectTopic.add(idParent+"");
                }
            }
        }

        // Pass validation
        mProgressDialog.setMessage("Menyimpan perubahan...");
        if(!mProgressDialog.isShowing()) mProgressDialog.show();

        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        // convert your list to json
        String jsonSubjectList = new Gson().toJson(mSavedSubjectTopic);

        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id_user", mUserLogin.getId()+"")
                .addFormDataPart("min_price_rate", minPrice+"")
                .addFormDataPart("max_travel_distance", maxDistance+"")
                .addFormDataPart("subjects", null, RequestBody.create(MEDIA_TYPE_JSON, jsonSubjectList))
                .build();

        OkHttpClient httpClient = new OkHttpClient();

        Request httpRequest = new Request.Builder()
                .url(App.URL_EDIT_TUTOR_PREF)
                .post(formBody)
                .build();

        Call httpCall = httpClient.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // alert user
                getActivity().runOnUiThread(new DialogMessage(mContext, dialogTitle, String.valueOf(e), mProgressDialog));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                if(response.isSuccessful() && response.code() == 200){
                    try {
                        JSONObject responseObj = new JSONObject(responseStr);
                        int status = responseObj.getInt("success");

                        if(status == 1){
                            // Retrieve user data from http response
                            String userData = responseObj.getString("data");
                            JSONObject objUserData = new JSONObject(userData);
                            // Store in Session w/ User object
                            mUserLogin.setMinPriceRate(objUserData.getInt(mSession.KEY_MIN_PRICE_RATE));
                            mUserLogin.setMaxTravelDistance(objUserData.getInt(mSession.KEY_MAX_TRAVEL_DISTANCE));
                            Type type = new TypeToken<Map<String, SavedSubject>>(){}.getType();
                            Map<String, SavedSubject> subjectMap = new Gson().fromJson(objUserData.getString(mSession.KEY_SUBJECTS), type);
                            if(subjectMap != null) {
                                HashMap<String, SavedSubject> subjectHashmap = new HashMap<String, SavedSubject>(subjectMap); // cast process
                                mUserLogin.setSubjects(subjectHashmap);
                            }else{
                                mUserLogin.setSubjects(new HashMap<String, SavedSubject>());
                            }
                            mSession.updateSession(mUserLogin);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(mProgressDialog.isShowing()) mProgressDialog.hide();
                                    Toast.makeText(mContext, "Pengaturan berhasil disimpan", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            String message = responseObj.getString("error_msg");
                            // alert user
                            getActivity().runOnUiThread(new DialogMessage(mContext, dialogTitle, message, mProgressDialog));
                        }
                    } catch (JSONException e) {
                        // alert user
                        getActivity().runOnUiThread(new DialogMessage(mContext, dialogTitle, e.getMessage(), mProgressDialog));
                    }
                }else{
                    // alert user
                    getActivity().runOnUiThread(new DialogMessage(mContext, dialogTitle, response.message(), mProgressDialog));
                }
            }
        });
    }
}

