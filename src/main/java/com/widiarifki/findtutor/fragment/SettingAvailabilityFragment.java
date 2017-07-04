package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.AvailabilityAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.helper.SessionManager;
import com.widiarifki.findtutor.model.Day;
import com.widiarifki.findtutor.model.User;

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
import okhttp3.Response;

/**
 * Created by widiarifki on 09/06/2017.
 */

public class SettingAvailabilityFragment extends Fragment {

    Fragment mThisFragment;
    private Context mContext;
    Activity mContextActivity;
    private List<Day> mDays;
    SessionManager mSessionManager;
    User mUserLogin;
    ProgressDialog mProgressDialog;
    RadioGroup mAvailabilityOpt;
    ListView mListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThisFragment = this;
        mDays = new ArrayList<Day>();
        mDays.add(new Day(0, "Senin"));
        mDays.add(new Day(1, "Selasa"));
        mDays.add(new Day(2, "Rabu"));
        mDays.add(new Day(3, "Kamis"));
        mDays.add(new Day(4, "Jumat"));
        mDays.add(new Day(5, "Sabtu"));
        mDays.add(new Day(6, "Minggu"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        mContextActivity = (Activity) mContext;
        mSessionManager = new SessionManager(mContext);
        mUserLogin = mSessionManager.getUserDetail();

        View view = inflater.inflate(R.layout.fragment_setting_availability, container, false);
        mProgressDialog = new ProgressDialog(mContext);

        mListView = (ListView) view.findViewById(R.id.lvDays);
        mListView.setAdapter(new AvailabilityAdapter(mContext, mDays));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Day day = mDays.get(position);
                Fragment fragment = new SetTimeAvailabilityFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("idDay", day.getId());
                fragment.setArguments(bundle);
                ((MainActivity)getContext()).addStackedFragment(fragment, "Jadwal Hari " + day.getName(), getString(R.string.menu_availibility));
                //((MainActivity)getContext()).addStackedFragment(mThisFragment, fragment, "Jadwal Hari " + day.getName(), getString(R.string.menu_availibility));
            }
        });

        mAvailabilityOpt = (RadioGroup) view.findViewById(R.id.rgrup_availability_opt);

        bindInitialData();

        return view;
    }

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

            if(checkedId == R.id.radio_opt_available)
                updateAvailability(Constants.VALUE_KEY_AVAILABLE);
            else if(checkedId == R.id.radio_opt_unavailable)
                updateAvailability(Constants.VALUE_KEY_UNAVAILABLE);
            else if(checkedId == R.id.radio_opt_scheduled)
                updateAvailability(Constants.VALUE_KEY_SCHEDULED);

        }
    };

    private void bindInitialData() {
        mAvailabilityOpt.setOnCheckedChangeListener(null);
        updateSelectedRadio();
        mAvailabilityOpt.setOnCheckedChangeListener(checkedChangeListener);
    }

    private void updateSelectedRadio() {
        int isAvailable = mUserLogin.getIsAvailable();
        RadioButton selectedRadio = null;
        if(isAvailable == Constants.VALUE_KEY_AVAILABLE){
            selectedRadio = (RadioButton) mAvailabilityOpt.findViewById(R.id.radio_opt_available);
            mListView.setBackgroundColor(getResources().getColor(R.color.disableLvBg));
        }
        else if(isAvailable == Constants.VALUE_KEY_UNAVAILABLE){
            selectedRadio = (RadioButton) mAvailabilityOpt.findViewById(R.id.radio_opt_unavailable);
            mListView.setBackgroundColor(getResources().getColor(R.color.disableLvBg));
        }
        else if(isAvailable == Constants.VALUE_KEY_SCHEDULED){
            selectedRadio = (RadioButton) mAvailabilityOpt.findViewById(R.id.radio_opt_scheduled);
            mListView.setBackgroundColor(getResources().getColor(R.color.defaultLvBg));
        }
        selectedRadio.setChecked(true);
    }

    void updateAvailability(int status){
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Menyimpan..");
        if(!mProgressDialog.isShowing()) mProgressDialog.show();

        FormBody formBody = new FormBody.Builder()
                .add("id_user", mUserLogin.getId()+"")
                .add("is_available", status+"")
                .build();

        OkHttpClient httpClient = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_SET_AVAILABILITY)
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
                        JSONObject jsonObj = new JSONObject(json);
                        if(jsonObj.getInt("success") == 1){
                            JSONObject data = jsonObj.getJSONObject("data");
                            mUserLogin.setIsAvailable(data.getInt(SessionManager.KEY_IS_AVAILABLE));
                            mSessionManager.updateSession(mUserLogin);
                            mContextActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateSelectedRadio();
                                    if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
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
}
