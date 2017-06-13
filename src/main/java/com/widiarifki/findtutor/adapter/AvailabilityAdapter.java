package com.widiarifki.findtutor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.model.Day;

import java.util.List;

/**
 * Created by widiarifki on 09/06/2017.
 */

public class AvailabilityAdapter extends ArrayAdapter<Day> {

    private Context mContext;
    private List<Day> mDays;

    public AvailabilityAdapter(@NonNull Context context, @NonNull List<Day> objects) {
        super(context, 0, objects);
        mContext = context;
        mDays = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout_availability_list, null);
        }

        TextView tvDay = (TextView) convertView.findViewById(R.id.text_day);

        Day day = mDays.get(position);
        if(day != null){
            tvDay.setText(day.getName());
        }

        return convertView;
    }
}
