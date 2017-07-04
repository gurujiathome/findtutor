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
import com.widiarifki.findtutor.helper.SessionManager;
import com.widiarifki.findtutor.model.AvailabilityPerDay;
import com.widiarifki.findtutor.model.Day;
import com.widiarifki.findtutor.model.User;

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
    HashMap<String, List<AvailabilityPerDay>> mUserAvailability;

    public AvailabilityAdapter(@NonNull Context context, @NonNull List<Day> objects) {
        super(context, 0, objects);
        mContext = context;
        mSessionManager = new SessionManager(mContext);
        mDays = objects;
        mUserLogin = mSessionManager.getUserDetail();
        if(mUserLogin.getAvailabilities() != null){
            mUserAvailability = mUserLogin.getAvailabilities();
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout_availability_list, null);
        }

        TextView tvDay = (TextView) convertView.findViewById(R.id.text_day);
        TextView tvTime = (TextView) convertView.findViewById(R.id.text_time);

        Day day = mDays.get(position);
        if(day != null){
            tvDay.setText(day.getName());
        }

        tvTime.setText("Tidak ada jadwal");
        if(mUserAvailability != null) {
            int dayId = day.getId();
            if (mUserAvailability.get(dayId + "") != null) {
                List<AvailabilityPerDay> times = mUserAvailability.get(dayId + "");
                if(times.size() > 0) {
                    String[] displayTime = new String[times.size()];
                    int i = 0;
                    for (AvailabilityPerDay time : times) {
                        displayTime[i] = time.getStartHour() + "-" + time.getEndHour();
                        i++;
                    }
                    tvTime.setText(TextUtils.join(", ", displayTime));
                }
            }
        }

        return convertView;
    }
}
