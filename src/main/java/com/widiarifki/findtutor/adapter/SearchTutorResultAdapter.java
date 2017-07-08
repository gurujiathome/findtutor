package com.widiarifki.findtutor.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.fragment.TutorInfoFragment;
import com.widiarifki.findtutor.helper.CircleTransform;
import com.widiarifki.findtutor.model.User;

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
        viewHolder.tvName.setText(dataItem.getName());
        viewHolder.tvDistance.setText(dataItem.getDistanceFromRequestor()+"");
        Picasso.with(mContext).load(App.URL_PATH_PHOTO + dataItem.getPhotoUrl())
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(viewHolder.imgvUserPhoto);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvDistance;
        public ImageView imgvUserPhoto;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
            imgvUserPhoto = (ImageView) itemView.findViewById(R.id.imgvUserPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
