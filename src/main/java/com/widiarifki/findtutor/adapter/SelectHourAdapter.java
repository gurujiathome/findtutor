package com.widiarifki.findtutor.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.fragment.BookTutorFragment;
import com.widiarifki.findtutor.fragment.SettingAvailabilityTimeFragment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by widiarifki on 05/07/2017.
 */

public class SelectHourAdapter extends RecyclerView.Adapter {

    Context mContext;
    HashMap<Integer, String> mTimes;
    List<Integer> mSelectedTimes;
    Integer[] mEnabledTimes;
    Integer[] mDisabledTimes;
    Fragment mFragment;

    private HashMap<Integer, String> setTimes(){
        HashMap<Integer, String> times = new HashMap<Integer, String>();
        for (int i=0; i<24; i++){
            String label = String.format("%02d:00 - %02d:00", i, i+1);
            times.put(i, label);
        }
        return times;
    }

    /** Constructor to be called from BOOK TUTOR FRAGMENT **/
    public SelectHourAdapter(Context context, Fragment fragment, Integer[] tutorTimes, int availabilityStatus) {
        mTimes = setTimes();
        mContext = context;
        mFragment = fragment;
        if(availabilityStatus == Constants.TUTOR_AVAILABLE_BY_SCHEDULE) {
            mEnabledTimes = tutorTimes;
        }
        else if(availabilityStatus == Constants.TUTOR_AVAILABLE){
            mDisabledTimes = tutorTimes;
        }
    }

    /** Constructor to be called from setting availability **/
    public SelectHourAdapter(Context context, Fragment fragment, List<Integer> selectedTimes) {
        mTimes = setTimes();
        mContext = context;
        mFragment = fragment;
        mSelectedTimes = selectedTimes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_hour_picker, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myHolder = (MyViewHolder) holder;
        String label = mTimes.get(position);

        /** In case adapter called from BookTutorFragment, check if time label is adjust to user setting **/
        if(mEnabledTimes != null && mEnabledTimes.length > 0) {
            if(!Arrays.asList(mEnabledTimes).contains(position)){
                myHolder.toggle.setEnabled(false);
            }
        }

        if(mDisabledTimes != null && mDisabledTimes.length > 0) {
            if(Arrays.asList(mDisabledTimes).contains(position)){
                myHolder.toggle.setEnabled(false);
            }
        }

        if(mSelectedTimes != null) {
            if (mSelectedTimes.contains(position)) myHolder.toggle.setChecked(true);
        }

        myHolder.toggle.setText(label);
        myHolder.toggle.setTextOn(label);
        myHolder.toggle.setTextOff(label);
        myHolder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mFragment instanceof SettingAvailabilityTimeFragment){
                    SettingAvailabilityTimeFragment timeFragment = (SettingAvailabilityTimeFragment) mFragment;
                    if(isChecked) {
                        timeFragment.addTime(position);
                    }else{
                        timeFragment.removeTime(position);
                    }
                }
                else if(mFragment instanceof BookTutorFragment){
                    BookTutorFragment fragment = (BookTutorFragment) mFragment;
                    if(isChecked) {
                        /** Set start time #1 **/
                        /*if(fragment.getStartTime() == 0 && fragment.getEndTime() == 0){
                            fragment.setStartTime(position);
                            fragment.setEndTime(position);
                        }
                        else{
                            fragment.setEndTime(position);
                        }*/
                        fragment.addTime(position);
                    }else{
                        /** If removed start time **/
                        /*if(position == fragment.getStartTime()){
                            fragment.setStartTime(0);
                            fragment.setEndTime(0);
                        }else{
                            fragment.setEndTime();
                        }*/
                        fragment.removeTime(position);
                    }
                }
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ToggleButton toggle;

        public MyViewHolder(View itemView) {
            super(itemView);
            toggle = (ToggleButton) itemView.findViewById(R.id.toggleBtn);
        }
    }

    @Override
    public int getItemCount() {
        return mTimes.size();
    }
}
