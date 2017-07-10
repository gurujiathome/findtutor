package com.widiarifki.findtutor.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.helper.CircleTransform;
import com.widiarifki.findtutor.model.Session;

import java.util.List;

/**
 * Created by widiarifki on 09/07/2017.
 */

public class SessionPreviousAdapter extends RecyclerView.Adapter {

    Context mContext;
    List<Session> mSessionList;
    ProgressDialog mProgressDialog;

    public SessionPreviousAdapter(Context context, List<Session> listSession) {
        mContext = context;
        mSessionList = listSession;
        mProgressDialog = new ProgressDialog(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_session_previous, null);
        return new SessionPendingAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Session dataItem = mSessionList.get(position);
        String name = dataItem.getUser().getName();
        String subject = dataItem.getSubject();
        String schedule = dataItem.getScheduleDate() + " " + dataItem.getStartHour() + " " + dataItem.getEndHour();
        String location = dataItem.getLocationAddress() + " (" + dataItem.getDistanceBetween() + " km)";
        String photoUrl = App.URL_PATH_PHOTO + dataItem.getUser().getPhotoUrl();
        RequestCreator photoRequest = Picasso.with(mContext).load(photoUrl)
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp);

        SessionPendingAdapter.MyViewHolder viewHolder = (SessionPendingAdapter.MyViewHolder) holder;
        photoRequest.into(viewHolder.imgvUserPhoto);
        viewHolder.tvName.setText(name);
        viewHolder.tvSubjectName.setText(subject);
        viewHolder.tvSchedule.setText(schedule);
        viewHolder.tvLocation.setText(location);
    }

    @Override
    public int getItemCount() {
        return mSessionList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgvUserPhoto;
        public TextView tvName;
        public TextView tvSubjectName;
        public TextView tvSchedule;
        public TextView tvLocation;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgvUserPhoto = (ImageView) itemView.findViewById(R.id.imgvUserPhoto);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvSubjectName = (TextView) itemView.findViewById(R.id.tvSubjectName);
            tvSchedule = (TextView) itemView.findViewById(R.id.tvSchedule);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
        }
    }
}
