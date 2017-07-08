package com.widiarifki.findtutor.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.AvailabilityTimeAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.helper.RunnableDialogMessage;
import com.widiarifki.findtutor.model.AvailabilityPerDay;
import com.widiarifki.findtutor.model.User;
import com.widiarifki.findtutor.view.MyNumberPicker;

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
 * Created by widiarifki on 09/06/2017.
 */

public class SetTimeAvailabilityFragment extends Fragment {

    private int mIdDay;
    private Context mContext;
    private SessionManager mSession;
    private User mUserLogin;
    private List<AvailabilityPerDay> mDayAvailabilities;
    private AvailabilityTimeAdapter mListAdapter;
    HashMap<String, List<AvailabilityPerDay>> mUserAvailability;

    private TextView tvStartTime;
    private TextView tvEndTime;
    private ImageButton btnSaveTime;
    private ListView lvAvailability;

    ProgressDialog mProgressDialog;

    static int ACTION_CODE_START_TIME = 1;
    static int ACTION_CODE_END_TIME = 2;
    int mNextHourVal = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        mSession = new SessionManager(mContext);
        mUserLogin = mSession.getUserDetail();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);

        Bundle bundle = getArguments();
        mIdDay = bundle.getInt("idDay", 0);

        View view = inflater.inflate(R.layout.fragment_set_time_availability, container, false);

        if(mUserLogin.getAvailabilities() == null){
            mUserAvailability = new HashMap<String, List<AvailabilityPerDay>>();
            mUserLogin.setAvailabilities(mUserAvailability);
        }else{
            mUserAvailability = mUserLogin.getAvailabilities();
        }

        if(mUserAvailability.get(mIdDay+"") != null){
            mDayAvailabilities = mUserAvailability.get(mIdDay+"");
        }else {
            mDayAvailabilities = new ArrayList<AvailabilityPerDay>();
        }
        mListAdapter = new AvailabilityTimeAdapter(mContext, mDayAvailabilities);
        lvAvailability = (ListView) view.findViewById(R.id.lvAvailability);
        lvAvailability.setAdapter(mListAdapter);
        App.setListViewHeightBasedOnChildren(lvAvailability);

        btnSaveTime = (ImageButton) view.findViewById(R.id.btn_save_time);
        btnSaveTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSave();
            }
        });
        tvStartTime = (TextView) view.findViewById(R.id.text_start_time);
        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputTime(tvStartTime, ACTION_CODE_START_TIME);
            }
        });
        tvEndTime = (TextView) view.findViewById(R.id.text_end_time);
        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputTime(tvEndTime, ACTION_CODE_END_TIME);
            }
        });
        tvStartTime.setText("--:--");
        tvEndTime.setText("--:--");

        return view;
    }

    private void attemptSave() {
        final String startHour = tvStartTime.getText().toString();
        final String endHour = tvEndTime.getText().toString();

        // Pass validation
        mProgressDialog.setMessage("Menyimpan data...");
        if(!mProgressDialog.isShowing()) mProgressDialog.show();

        RequestBody formBody = new FormBody.Builder()
                .add("id_user", mUserLogin.getId()+"")
                .add("day", mIdDay+"")
                .add("start_hour", startHour)
                .add("end_hour", endHour)
                .build();

        OkHttpClient httpClient = new OkHttpClient();

        Request httpRequest = new Request.Builder()
                .url(App.URL_ADD_AVAILABILITY)
                .post(formBody)
                .build();

        Call httpCall = httpClient.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // alert user
                getActivity().runOnUiThread(new RunnableDialogMessage(mContext, "Tambah Data Gagal", String.valueOf(e), mProgressDialog));
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
                            final int id = responseObj.getInt("id");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mListAdapter.addToList(new AvailabilityPerDay(id, mIdDay, startHour, endHour));
                                    App.setListViewHeightBasedOnChildren(lvAvailability);
                                    if(mUserAvailability.get(mIdDay) == null){
                                        mUserAvailability.put(mIdDay+"", mDayAvailabilities);
                                    }
                                    mSession.updateSession(mUserLogin);
                                    if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
                                    tvStartTime.setText("--:--");
                                    tvEndTime.setText("--:--");
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
                    getActivity().runOnUiThread(new RunnableDialogMessage(mContext, "Tambah Data Gagal", response.message(), mProgressDialog));
                }
            }
        });
    }

    private void inputTime(final TextView textView, int actionCode) {
        final String time = textView.getText()+"";
        String[] timeSplit = time.split(":");
        String timeHour = timeSplit[0];
        String timeMin = timeSplit[1];

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_input_time, null);
        final MyNumberPicker hourpicker = (MyNumberPicker) dialogView.findViewById(R.id.hourpicker);
        final MyNumberPicker minpicker = (MyNumberPicker) dialogView.findViewById(R.id.minpicker);
        int defaultVal = (mNextHourVal);
        hourpicker.setMinValue(defaultVal);
        hourpicker.setValue(defaultVal);
        //hourpicker.setValue(Integer.parseInt(timeHour));
        //minpicker.setValue(Integer.parseInt(timeMin));
        /*final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timepicker_start);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                int TIME_PICKER_INTERVAL=15;
                boolean mIgnoreEvent=false;
                if (mIgnoreEvent)
                    return;
                if (minute%TIME_PICKER_INTERVAL!=0){
                    int minuteFloor=minute-(minute%TIME_PICKER_INTERVAL);
                    minute=minuteFloor + (minute==minuteFloor+1 ? TIME_PICKER_INTERVAL : 0);
                    if (minute==60)
                        minute=0;
                    mIgnoreEvent=true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        view.setMinute(minute);
                    else
                        view.setCurrentMinute(minute);
                    mIgnoreEvent=false;
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(Integer.parseInt(timeHour));
            timePicker.setMinute(Integer.parseInt(timeMin));
        }else{
            timePicker.setCurrentHour(Integer.parseInt(timeHour));
            timePicker.setCurrentMinute(Integer.parseInt(timeMin));
        }*/

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Dari Jam");
        dialogBuilder.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int pickedHour, pickedMin;
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    //pickedHour = timePicker.getHour();
                    //pickedMin = timePicker.getMinute();
                }else {
                    //pickedHour = timePicker.getCurrentHour();
                    //pickedMin = timePicker.getCurrentMinute();
                }*/
                pickedHour = hourpicker.getValue();
                String[] minsVal = minpicker.getDisplayedValues();
                pickedMin = Integer.parseInt(minsVal[minpicker.getValue() - 1]);
                textView.setText(String.format("%02d:%02d", pickedHour, pickedMin));
                mNextHourVal = pickedHour + 1;

            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        if(!alertDialog.isShowing()) alertDialog.show();
    }
}
