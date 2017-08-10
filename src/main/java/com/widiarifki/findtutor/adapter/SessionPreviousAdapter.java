package com.widiarifki.findtutor.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.fragment.SessionOverviewFragment;
import com.widiarifki.findtutor.helper.CircleTransform;
import com.widiarifki.findtutor.model.Session;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by widiarifki on 09/07/2017.
 */

public class SessionPreviousAdapter extends RecyclerView.Adapter {

    private final Fragment mFragment;
    private final int mContextUser;
    Context mContext;
    List<Session> mSessionList;
    ProgressDialog mProgressDialog;

    public SessionPreviousAdapter(Context context, List<Session> listSession, Fragment fragment, int contextUser) {
        mContext = context;
        mSessionList = listSession;
        mProgressDialog = new ProgressDialog(mContext);
        mFragment = fragment;
        mContextUser = contextUser;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_session_previous, null);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = ((RecyclerView)parent).getChildLayoutPosition(v);
                Session sess = mSessionList.get(position);
                if(sess.getStatus() == Constants.SESSION_FINISHED)
                    onClickItem(sess);
            }
        });
        return new MyViewHolder(view);
    }

    private void onClickItem(Session session) {
        if(mContext instanceof MainActivity) {
            if(mFragment != null) {
                Bundle params = new Bundle();
                params.putInt("context_user", mContextUser);
                params.putInt("id_session", session.getId());

                Fragment fragment = new SessionOverviewFragment();
                fragment.setArguments(params);
                ((MainActivity) mContext).addStackedFragment(
                        mFragment,
                        fragment,
                        mContext.getString(R.string.title_session_overview),
                        mContext.getString(R.string.menu_session_history));
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Session dataItem = mSessionList.get(position);
        String name = dataItem.getUser().getName();
        String subject = dataItem.getSubject();
        String schedule = "";
        String time = "";
        if(dataItem.getStatus() == Constants.SESSION_FINISHED) {
            schedule = dataItem.getDateHeld();
            time = dataItem.getStartHourHeld() + " - " + dataItem.getEndHourHeld();
        }else{
            schedule = App.convertDateFormat("yyyy-MM-dd", "EEE, d MMM yyyy", dataItem.getScheduleDate());
            time = TextUtils.substring(dataItem.getStartHour(), 0, 5) + " - " + TextUtils.substring(dataItem.getEndHour(), 0, 5);
        }
        DecimalFormat df = new DecimalFormat("#.#");
        String location = dataItem.getLocationAddress() + " (" + df.format(dataItem.getDistanceBetween()) + " km)";
        String photoUrl = App.URL_PATH_PHOTO + dataItem.getUser().getPhotoUrl();
        RequestCreator photoRequest = Picasso.with(mContext).load(photoUrl)
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp);

        MyViewHolder viewHolder = (MyViewHolder) holder;
        photoRequest.into(viewHolder.imgvUserPhoto);
        viewHolder.tvName.setText(name);
        viewHolder.tvSubjectName.setText(subject);
        viewHolder.tvSchedule.setText(schedule);
        viewHolder.tvTime.setText(time);
        if(dataItem.getStatus() == Constants.SESSION_FINISHED){
            viewHolder.layoutFee.setVisibility(View.VISIBLE);
            viewHolder.tvFee.setText(dataItem.getTutorFee());
        }else{
            viewHolder.layoutFee.setVisibility(View.GONE);
        }
        viewHolder.tvLocation.setText(location);
        if(dataItem.getStatus() == Constants.SESSION_REJECTED){
            viewHolder.tvStatus.setText(mContext.getString(R.string.session_rejected_by_tutor));
        }
        else if(dataItem.getStatus() == Constants.SESSION_CANCELED_BY_TUTOR){
            viewHolder.tvStatus.setText(mContext.getString(R.string.session_canceled_by_tutor));
        }
        else if(dataItem.getStatus() == Constants.SESSION_CANCELED_BY_STUDENT){
            viewHolder.tvStatus.setText(mContext.getString(R.string.session_canceled_by_student));
        }
        else if(dataItem.getStatus() == Constants.SESSION_FINISHED){
            viewHolder.tvStatus.setText(mContext.getString(R.string.session_finished));
        }
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
        public TextView tvTime;
        public TextView tvFee;
        public LinearLayout layoutFee;
        public TextView tvLocation;
        public TextView tvStatus;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgvUserPhoto = (ImageView) itemView.findViewById(R.id.imgvUserPhoto);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvSubjectName = (TextView) itemView.findViewById(R.id.tvSubjectName);
            tvSchedule = (TextView) itemView.findViewById(R.id.tvSchedule);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvFee = (TextView) itemView.findViewById(R.id.tvFee);
            layoutFee = (LinearLayout) itemView.findViewById(R.id.layoutFee);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
        }
    }
}
