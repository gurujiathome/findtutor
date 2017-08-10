package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.rits.cloning.Cloner;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.SelectHourAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.helper.RunnableDialogMessage;
import com.widiarifki.findtutor.model.Day;
import com.widiarifki.findtutor.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by widiarifki on 05/07/2017.
 */

public class SettingAvailabilityTimeFragment extends Fragment {

    Fragment mThisFragment;
    private Context mContext;
    private Activity mContextActivity;
    static List<Integer> mSelectedTimes;
    private SessionManager mSession;
    private User mUserLogin;
    private HashMap<String, List<Integer>> mUserTimeslots;
    private ProgressDialog mProgressDialog;
    private int mIdDay;
    AlertDialog mDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mThisFragment = this;
        mSelectedTimes = new ArrayList<Integer>();

        Bundle bundle = getArguments();
        mIdDay = bundle.getInt(Constants.PARAM_KEY_DAY, 0);
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
            saveChanges();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mContextActivity = (Activity) mContext;
        mSession = new SessionManager(mContext);
        mUserLogin = mSession.getUserDetail();
        if(mUserLogin.getTimeslots() != null){
            mUserTimeslots = mUserLogin.getTimeslots();
            if(mUserTimeslots.containsKey(mIdDay+"")){
                Cloner cloner = new Cloner();
                mSelectedTimes = cloner.deepClone(mUserTimeslots.get(mIdDay+""));
            }
        }

        View view = inflater.inflate(R.layout.fragment_setting_availability_time, container, false);

        final RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        rv.setAdapter(new SelectHourAdapter(mContext, this, mSelectedTimes));
        rv.setLayoutManager(new GridLayoutManager(mContext, 3));
        Button btnCopyTime = (Button) view.findViewById(R.id.btnCopyTime);
        btnCopyTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Day> days = new ArrayList<Day>();
                final ArrayList<Day> displayedDays = new ArrayList<Day>();
                days.add(new Day(0, "Senin"));
                days.add(new Day(1, "Selasa"));
                days.add(new Day(2, "Rabu"));
                days.add(new Day(3, "Kamis"));
                days.add(new Day(4, "Jumat"));
                days.add(new Day(5, "Sabtu"));
                days.add(new Day(6, "Minggu"));
                String[] stringArray = new String[days.size() - 1];
                int i = 0;
                for (Day day : days){
                    if(day.getId() != mIdDay) {
                        stringArray[i] = day.getName();
                        displayedDays.add(day);
                        i++;
                    }
                }
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);

                ListView modeList = new ListView(mContext);
                modeList.setAdapter(modeAdapter);
                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Day day = displayedDays.get(position);
                        if(mUserLogin.getTimeslots() != null){
                            mUserTimeslots = mUserLogin.getTimeslots();
                            if(mUserTimeslots.containsKey(day.getId()+"")){
                                List<Integer> mSelectedTimesFromDay = mUserTimeslots.get(day.getId() + "");
                                if(!mSelectedTimesFromDay.equals(mSelectedTimes)) {
                                    mSelectedTimes.clear();
                                    mSelectedTimes.addAll(mSelectedTimesFromDay);
                                    rv.swapAdapter(new SelectHourAdapter(mContext, mThisFragment, mSelectedTimes), false);
                                }
                            }
                        }
                        mDialog.dismiss();
                    }
                });

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext)
                        .setTitle("Pilih Hari")
                        .setView(modeList);
                mDialog = dialogBuilder.create();
                mDialog.show();

            }
        });

        mProgressDialog = new ProgressDialog(mContext);

        return view;
    }

    private void saveChanges() {
        // If no selection before, and no add time
        if(mSelectedTimes.size() == 0) {
            if (mUserTimeslots == null || !mUserTimeslots.containsKey(mIdDay + "")) {
                return;
            }else{
                if (mUserTimeslots.get(mIdDay + "").isEmpty()) {
                    return;
                }
            }
        }
        // Pass validation
        mProgressDialog.setMessage("Menyimpan data...");
        mProgressDialog.setCancelable(false);
        if(!mProgressDialog.isShowing()) mProgressDialog.show();

        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        // convert your list to json
        String jsonTimeList = new Gson().toJson(mSelectedTimes);

        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id_user", mUserLogin.getId()+"")
                .addFormDataPart("day", mIdDay+"")
                .addFormDataPart("timeslots", null, RequestBody.create(MEDIA_TYPE_JSON, jsonTimeList))
                .build();

        OkHttpClient httpClient = new OkHttpClient();

        Request httpRequest = new Request.Builder()
                .url(App.URL_SAVE_TUTOR_TIMESLOT)
                .post(formBody)
                .build();

        Call httpCall = httpClient.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // alert user
                mContextActivity.runOnUiThread(new RunnableDialogMessage(mContext, "Simpan Data Gagal", String.valueOf(e), mProgressDialog));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                if(response.isSuccessful() && response.code() == 200){
                    JSONObject responseObj = null;
                    try {
                        responseObj = new JSONObject(responseStr);
                        int status = responseObj.getInt("success");
                        if(status == 1){
                            mContextActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(mUserTimeslots != null){
                                        if(mUserTimeslots.containsKey(mIdDay+"")) {
                                            mUserTimeslots.remove(mIdDay + "");
                                        }
                                    }else{
                                        mUserTimeslots = new HashMap<String, List<Integer>>();
                                        mUserLogin.setTimeslots(mUserTimeslots);
                                    }

                                    if(mSelectedTimes.size() > 0) {
                                        mUserTimeslots.put(mIdDay + "", mSelectedTimes);
                                    }
                                    mSession.updateSession(mUserLogin);
                                    if(mProgressDialog.isShowing()) mProgressDialog.dismiss();

                                    ((MainActivity)mContext).removeFragmentFromStack(mThisFragment);
                                }
                            });
                        }else{
                            String message = responseObj.getString("error_msg");
                            // alert user
                            new RunnableDialogMessage(mContext, "Tambah Data Gagal", message, mProgressDialog);
                        }
                    } catch (JSONException e) {
                        // alert user
                        new RunnableDialogMessage(mContext, "Tambah Data Gagal", e.getMessage(), mProgressDialog);
                    }
                }else{
                    // alert user
                    mContextActivity.runOnUiThread(new RunnableDialogMessage(mContext, "Tambah Data Gagal", response.message(), mProgressDialog));
                }
            }
        });
    }

    public static void addTime(int timeId){
        Integer value = Integer.valueOf(timeId);
        if(!mSelectedTimes.contains(value))
            mSelectedTimes.add(value);
    }

    public static void removeTime(int timeId){
        Integer value = Integer.valueOf(timeId);
        if(mSelectedTimes.contains(value)) {
            int index = mSelectedTimes.indexOf(value);
            mSelectedTimes.remove(index);
        }
    }
}
