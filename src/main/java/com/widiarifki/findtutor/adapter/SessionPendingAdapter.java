package com.widiarifki.findtutor.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.helper.CircleTransform;
import com.widiarifki.findtutor.model.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by widiarifki on 09/07/2017.
 */

public class SessionPendingAdapter extends RecyclerView.Adapter {

    Context mContext;
    List<Session> mSessionList;
    ProgressDialog mProgressDialog;

    public SessionPendingAdapter(Context context, List<Session> sessionList) {
        mContext = context;
        mSessionList = sessionList;
        mProgressDialog = new ProgressDialog(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_session_pending, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Session dataItem = mSessionList.get(position);
        final int idSession = dataItem.getId();
        final String name = dataItem.getUser().getName();
        final String subject = dataItem.getSubject();
        final String schedule = dataItem.getScheduleDate() + " " + dataItem.getStartHour() + " " + dataItem.getEndHour();
        final String location = dataItem.getLocationAddress() + " (" + dataItem.getDistanceBetween() + " km)";
        final String photoUrl = App.URL_PATH_PHOTO + dataItem.getUser().getPhotoUrl();
        final RequestCreator photoRequest = Picasso.with(mContext).load(photoUrl)
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp);

        MyViewHolder viewHolder = (MyViewHolder) holder;
        photoRequest.into(viewHolder.imgvUserPhoto);
        viewHolder.tvName.setText(name);
        viewHolder.tvSubjectName.setText(subject);
        viewHolder.tvSchedule.setText(schedule);
        viewHolder.tvLocation.setText(location);
        viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_accept_confirmation, null);
                ((TextView)dialogView.findViewById(R.id.tvConfirmMessage)).setText(mContext.getString(R.string.confirm_message_accept_session));
                ((TextView)dialogView.findViewById(R.id.tvName)).setText(name);
                ((TextView)dialogView.findViewById(R.id.tvSubjectName)).setText(subject);
                ((TextView)dialogView.findViewById(R.id.tvSchedule)).setText(schedule);
                ((TextView)dialogView.findViewById(R.id.tvLocation)).setText(location);
                photoRequest.into((ImageView) dialogView.findViewById(R.id.imgvUserPhoto));
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext)
                        //.setTitle(mContext.getString(R.string.title_session_accept_confirmation))
                        .setView(dialogView)
                        .setNegativeButton(mContext.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(mContext.getString(R.string.action_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                processApproval(Constants.SESSION_ACCEPTED, idSession, position);
                            }
                        });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        viewHolder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_accept_confirmation, null);
                ((TextView)dialogView.findViewById(R.id.tvConfirmMessage)).setText(mContext.getString(R.string.confirm_message_reject_session));
                ((TextView)dialogView.findViewById(R.id.tvName)).setText(name);
                ((TextView)dialogView.findViewById(R.id.tvSubjectName)).setText(subject);
                ((TextView)dialogView.findViewById(R.id.tvSchedule)).setText(schedule);
                ((TextView)dialogView.findViewById(R.id.tvLocation)).setText(location);
                photoRequest.into((ImageView) dialogView.findViewById(R.id.imgvUserPhoto));
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext)
                        //.setTitle(mContext.getString(R.string.title_session_accept_confirmation))
                        .setView(dialogView)
                        .setNegativeButton(mContext.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(mContext.getString(R.string.action_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                processApproval(Constants.SESSION_REJECTED, idSession, position);
                            }
                        });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });
    }

    private void processApproval(final int approvalType, int idSession, final int dataIndex) {
        OkHttpClient httpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("id_session", idSession+"")
                .add(Constants.PARAM_KEY_APPROVAL_TYPE, approvalType+"")
                .build();
        Request httpRequest = new Request.Builder()
                .url(App.URL_POST_SESSION_APPROVAL)
                .post(formBody)
                .build();
        Call httpCall = httpClient.newCall(httpRequest);

        mProgressDialog.setMessage("Sedang memproses...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful() && response.code() == 200){
                    String json = response.body().string();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(json);
                        final int success = jsonObject.getInt("success");
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(success == 1){
                                    mProgressDialog.dismiss();
                                    String dialogTitle = "";
                                    String dialogMsg = "";
                                    if(approvalType == Constants.SESSION_ACCEPTED){
                                        dialogTitle = "Permintaan jadwal berhasil diterima";
                                        dialogMsg = "Anda dapat melihat daftar jadwal/sesi selanjutnya di tab \"Terkonfirmasi\"";
                                        App.showSimpleDialog(mContext, dialogTitle, dialogMsg);
                                    }
                                    else if(approvalType == Constants.SESSION_REJECTED){
                                        dialogTitle = "Permintaan jadwal berhasil ditolak";
                                        App.showSimpleDialog(mContext, dialogTitle);
                                    }
                                    mSessionList.remove(dataIndex);
                                    notifyDataSetChanged();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{

                }
            }
        });
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
        public Button btnReschedule;
        public Button btnAccept;
        public Button btnReject;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgvUserPhoto = (ImageView) itemView.findViewById(R.id.imgvUserPhoto);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvSubjectName = (TextView) itemView.findViewById(R.id.tvSubjectName);
            tvSchedule = (TextView) itemView.findViewById(R.id.tvSchedule);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            btnReschedule = (Button) itemView.findViewById(R.id.btnReschedule);
            btnAccept = (Button) itemView.findViewById(R.id.btnAccept);
            btnReject = (Button) itemView.findViewById(R.id.btnReject);
        }
    }
}
