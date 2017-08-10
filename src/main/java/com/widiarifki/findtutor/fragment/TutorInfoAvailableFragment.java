package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.AvailabilityAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.model.Day;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by widiarifki on 09/08/2017.
 */

public class TutorInfoAvailableFragment extends Fragment {

    TextView mTvAvailability;
    private ListView mListView;
    private Context mContext;
    private ArrayList<Day> mDays;
    private int mIdUser;
    private int mStatus;
    private Activity mContextActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle params = getArguments();
        mIdUser = params.getInt(Constants.PARAM_KEY_ID_USER, 0);
        mContext = getContext();
        mContextActivity = (Activity)mContext;
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
        View view = inflater.inflate(R.layout.fragment_tutor_info_availability, container, false);
        mTvAvailability = (TextView) view.findViewById(R.id.tvAvailability);
        mListView = (ListView) view.findViewById(R.id.lvDays);

        return view;
    }

    private void downloadData() {
        OkHttpClient httpClient = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_GET_TUTOR_TIMESLOT + "?id=" + mIdUser)
                .build();

        Call httpCall = httpClient.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful() && response.code() == 200) {
                    final String json = response.body().string();
                    try {
                        final JSONObject jsonObj = new JSONObject(json);
                        final JSONObject jsonData = jsonObj.getJSONObject("data");
                        final int availability = jsonData.getInt("is_available");
                        if(jsonData.getString("timeslots") != null) {
                            Type timeslotType = new TypeToken<Map<String, List<Integer>>>() {}.getType();
                            Map<String, List<Integer>> timeslotMap = new Gson().fromJson(jsonData.getString("timeslots"), timeslotType);
                            final HashMap<String, List<Integer>> timeslotHashmap = new HashMap<String, List<Integer>>(timeslotMap);
                            mContextActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mListView.setAdapter(new AvailabilityAdapter(mContext, mDays, timeslotHashmap));
                                    App.setListViewHeightBasedOnChildren(mListView);
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

    public void update(int status){
        String text = "";
        mStatus = status;
        if(status == 1){
            text = "Menyesuaikan permintaan";
        }else if(status == 2){
            text = "Sesuai jadwal dibawah";
            downloadData();
        }else{
            text = "Sedang tidak tersedia";
        }
        mTvAvailability.setText(text);
    }

}
