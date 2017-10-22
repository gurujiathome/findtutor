package com.widiarifki.findtutor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.model.Day;
import com.widiarifki.findtutor.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by widiarifki on 09/06/2017.
 */

public class AvailabilityAdapter extends ArrayAdapter<Day> {

    private Context mContext;
    private List<Day> mDays;
    SessionManager mSessionManager;
    User mUserLogin;
    HashMap<String, List<Integer>> mUserTimeslots;
    int mLayout = 0;

    public AvailabilityAdapter(@NonNull Context context, @NonNull List<Day> days) {
        super(context, 0, days);
        mContext = context;
        mSessionManager = new SessionManager(mContext);
        mDays = days;
        mUserLogin = mSessionManager.getUserDetail();
        if (mUserLogin.getTimeslots() != null) {
            mUserTimeslots = mUserLogin.getTimeslots();
        }
    }

    public AvailabilityAdapter(@NonNull Context context, @NonNull List<Day> days, @NonNull HashMap<String, List<Integer>> timeslots) {
        super(context, 0, days);
        mContext = context;
        mSessionManager = new SessionManager(mContext);
        mDays = days;
        mUserTimeslots = timeslots;
        mLayout = R.layout.item_list_tutor_availability_info;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            if(mLayout == 0)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_tutor_availability, null);
            else
                convertView = LayoutInflater.from(getContext()).inflate(mLayout, null);
        }

        TextView tvDay = (TextView) convertView.findViewById(R.id.text_day);
        TextView tvTime = (TextView) convertView.findViewById(R.id.text_time);

        Day day = mDays.get(position);
        if(day != null){
            tvDay.setText(day.getName());
        }

        tvTime.setText("Tidak ada jadwal");

        if(mUserTimeslots != null) {
            int dayId = day.getId();
            if (mUserTimeslots.get(dayId + "") != null) {
                List<Integer> times = mUserTimeslots.get(dayId + "");
                Collections.sort(times); // sort/order the list
                if(times.size() > 0) {
                    List<Integer> sequenceTimeStart = new ArrayList<Integer>();
                    List<Integer> sequenceTimeEnd = new ArrayList<Integer>();
                    int i = 0;
                    int latestTime = 0;
                    for(Integer timeId : times){
                        if(i == 0){
                            sequenceTimeStart.add(timeId);
                        }
                        else{
                            if(timeId == (latestTime+1)){

                            }else{
                                sequenceTimeEnd.add(latestTime);
                                sequenceTimeStart.add(timeId);
                            }
                        }

                        latestTime = timeId;

                        if(i == (times.size() - 1)){
                            sequenceTimeEnd.add(latestTime);
                        }
                        i++;
                    }

                    if(sequenceTimeStart.size() == sequenceTimeEnd.size()) {
                        List<String> displayTime = new ArrayList<String>();
                        int a = 0;
                        for (Integer time : sequenceTimeStart) {
                            String label = String.format("%02d:00 - %02d:00", time, sequenceTimeEnd.get(a) + 1);;
                            displayTime.add(label);
                            a++;
                        }
                        tvTime.setText(TextUtils.join(", ", displayTime));
                    }
                }
            }
        }

        return convertView;
    }
}
