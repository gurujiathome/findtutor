package com.widiarifki.findtutor.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.fragment.TutorInfoFragment;
import com.widiarifki.findtutor.helper.CircleTransform;
import com.widiarifki.findtutor.model.User;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by widiarifki on 30/06/2017.
 */

public class SearchTutorResultAdapter extends RecyclerView.Adapter {

    MainActivity mParentActivity;
    Context mContext;
    List<User> mUsers;
    Fragment mFragment;

    public SearchTutorResultAdapter(Context context, List<User> objects, Fragment fragment) {;
        mContext = context;
        mParentActivity = (MainActivity)mContext;
        mUsers = objects;
        mFragment = fragment;
    }

    public SearchTutorResultAdapter(Context context, List<User> objects) {;
        mContext = context;
        mParentActivity = (MainActivity)mContext;
        mUsers = objects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_tutor, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = ((RecyclerView)parent).getChildLayoutPosition(v);
                onClickItem(mUsers.get(position));
            }
        });
        return new MyViewHolder(view);
    }

    private void onClickItem(User user) {
        if(mContext instanceof MainActivity) {
            if(mFragment != null) {
                Bundle params = new Bundle();
                params.putInt(Constants.PARAM_KEY_ID_USER, user.getId());
                params.putString(Constants.PARAM_KEY_NAME, user.getName());
                params.putString(Constants.PARAM_KEY_PHOTO_URL, user.getPhotoUrl());
                params.putString(Constants.PARAM_KEY_LOCATION_ADDRESS, user.getLocationAddress());
                params.putString(Constants.PARAM_KEY_LATITUDE, user.getLatitude());
                params.putString(Constants.PARAM_KEY_LONGITUDE, user.getLongitude());
                params.putDouble(Constants.PARAM_KEY_DISTANCE, user.getDistanceFromRequestor());

                Fragment fragment = new TutorInfoFragment();
                fragment.setArguments(params);
                ((MainActivity) mContext).addStackedFragment(
                        mFragment,
                        fragment,
                        mContext.getString(R.string.title_tutor_profile),
                        mContext.getString(R.string.title_search_tutor_result));
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User dataItem = mUsers.get(position);
        MyViewHolder viewHolder = (MyViewHolder) holder;
        Picasso.with(mContext).load(App.URL_PATH_PHOTO + dataItem.getPhotoUrl())
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(viewHolder.imgvUserPhoto);
        viewHolder.tvName.setText(dataItem.getName());
        if(!dataItem.getLastSchool().isEmpty() && !dataItem.getLastSchool().equals("null"))
            viewHolder.tvEducation.setText(dataItem.getLastSchool()
                + (!dataItem.getLastSchoolDept().isEmpty() ? " - " + dataItem.getLastSchoolDept() : ""));
        else
            viewHolder.education.setVisibility(View.GONE);
        viewHolder.tvSubject.setText(dataItem.getSubjectStr());
        DecimalFormat df = new DecimalFormat("#.#");
        viewHolder.tvDistance.setText(df.format(dataItem.getDistanceFromRequestor()) + " km away");
        viewHolder.tvPriceRate.setText(dataItem.getMinPriceRate() + "/Jam");
        viewHolder.tutorRate.setRating((float) dataItem.getAvgRateOverall());
        viewHolder.tvTutorRate.setText(((float)dataItem.getAvgRateOverall())+"");
        if(dataItem.getHasBookedOnDate() > 0){
            viewHolder.sessionInfo.setVisibility(View.VISIBLE);
            viewHolder.tvSessionInfo.setText("Sudah memiliki " +dataItem.getHasBookedOnDate()+ " sesi pada hari ini");
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgvUserPhoto;
        public TextView tvName;
        public LinearLayout education;
        public TextView tvEducation;
        public TextView tvSubject;
        public TextView tvDistance;
        public TextView tvPriceRate;
        public RatingBar tutorRate;
        public TextView tvTutorRate;
        public LinearLayout sessionInfo;
        public TextView tvSessionInfo;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgvUserPhoto = (ImageView) itemView.findViewById(R.id.imgvUserPhoto);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            education = (LinearLayout) itemView.findViewById(R.id.education);
            tvEducation = (TextView) itemView.findViewById(R.id.tvEducation);
            tvSubject = (TextView) itemView.findViewById(R.id.tvSubject);
            tvPriceRate = (TextView) itemView.findViewById(R.id.tvPriceRate);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
            tutorRate = (RatingBar) itemView.findViewById(R.id.tutorRate);
            tvTutorRate = (TextView) itemView.findViewById(R.id.tvTutorRate);
            sessionInfo = (LinearLayout) itemView.findViewById(R.id.sessionInfo);
            tvSessionInfo = (TextView) itemView.findViewById(R.id.tvSessionInfo);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
