package com.widiarifki.findtutor.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.RateTutorActivity;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.helper.CircleTransform;
import com.widiarifki.findtutor.helper.RunnableDialogMessage;
import com.widiarifki.findtutor.model.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
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

public class SessionAcceptedAdapter extends RecyclerView.Adapter {

    Context mContext;
    List<Session> mSessionList;
    int mUserContextAs;
    ProgressDialog mProgressDialog;

    public SessionAcceptedAdapter(Context context, List<Session> sessionList, int userContextAs) {
        mContext = context;
        mSessionList = sessionList;
        mUserContextAs = userContextAs;
        mProgressDialog = new ProgressDialog(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_session_accepted, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Session dataItem = mSessionList.get(position);
        final int idSession = dataItem.getId();
        final String name = dataItem.getUser().getName();
        String subject = dataItem.getSubject();
        String displayDate = App.convertDateFormat("yyyy-MM-dd", "EEE, d MMM yyyy", dataItem.getScheduleDate());
        final String schedule = displayDate;
        String time = TextUtils.substring(dataItem.getStartHour(), 0, 5) + " - " + TextUtils.substring(dataItem.getEndHour(), 0, 5);
        DecimalFormat df = new DecimalFormat("#.#");
        String location = dataItem.getLocationAddress() + " (" + df.format(dataItem.getDistanceBetween()) + " km)";
        int status = dataItem.getStatus();
        String photoUrl = App.URL_PATH_PHOTO + dataItem.getUser().getPhotoUrl();
        RequestCreator photoRequest = Picasso.with(mContext).load(photoUrl)
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp);

        final MyViewHolder viewHolder = (MyViewHolder) holder;
        photoRequest.into(viewHolder.imgvUserPhoto);
        viewHolder.tvName.setText(name);
        viewHolder.tvSubjectName.setText(subject);
        viewHolder.tvSchedule.setText(schedule);
        viewHolder.tvTime.setText(time);
        viewHolder.tvLocation.setText(location);
        viewHolder.btnCancelSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext)
                        .setMessage("Anda yakin akan membatalkan jadwal sesi ini?")
                        .setNegativeButton(mContext.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(mContext.getString(R.string.action_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelSession(dataItem, viewHolder, position);
                            }
                        });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });
        viewHolder.btnStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.confirm_message_start_session))
                        .setNegativeButton(mContext.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(mContext.getString(R.string.action_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startSession(dataItem, viewHolder);
                            }
                        });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });
        viewHolder.btnEndSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.confirm_message_end_session))
                        .setNegativeButton(mContext.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(mContext.getString(R.string.action_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                endSession(dataItem);
                            }
                        });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        if(status == Constants.SESSION_STARTED){
            viewHolder.layoutBtn.setVisibility(View.GONE);
            viewHolder.layoutStartedStatus.setVisibility(View.VISIBLE);
            viewHolder.textStartedStatus.setVisibility(View.VISIBLE);
            if(mUserContextAs == Constants.SESSION_CONTEXT_AS_STUDENT){
                viewHolder.textStartedStatus.setText("Sesi belajar telah dimulai. Tap tombol \""+mContext.getString(R.string.action_end_session)+"\" saat sesi telah berakhir");
                viewHolder.btnStartSession.setVisibility(View.GONE);
                viewHolder.btnEndSession.setVisibility(View.VISIBLE);
            }else{
                viewHolder.textStartedStatus.setText("Sesi belajar telah dimulai.");
                viewHolder.btnStartSession.setVisibility(View.GONE);
                viewHolder.btnEndSession.setVisibility(View.GONE);
            }
        }else{
            viewHolder.layoutBtn.setVisibility(View.VISIBLE);
            if(mUserContextAs == Constants.SESSION_CONTEXT_AS_STUDENT){
                viewHolder.layoutStartedStatus.setVisibility(View.VISIBLE);
                viewHolder.textStartedStatus.setText("Tap tombol \""+mContext.getString(R.string.action_start_session)+"\" saat sesi belajar akan dimulai");
                viewHolder.btnStartSession.setVisibility(View.VISIBLE);
                viewHolder.btnEndSession.setVisibility(View.GONE);
            }else{
                viewHolder.layoutStartedStatus.setVisibility(View.GONE);
            }
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
        private final TextView tvTime;
        public TextView tvLocation;
        public Button btnStartSession;
        public Button btnCancelSession;
        public Button btnEndSession;
        public LinearLayout layoutBtn;
        public LinearLayout layoutStartedStatus;
        public TextView textStartedStatus;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgvUserPhoto = (ImageView) itemView.findViewById(R.id.imgvUserPhoto);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvSubjectName = (TextView) itemView.findViewById(R.id.tvSubjectName);
            tvSchedule = (TextView) itemView.findViewById(R.id.tvSchedule);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            btnStartSession = (Button) itemView.findViewById(R.id.btnStartSession);
            btnCancelSession = (Button) itemView.findViewById(R.id.btnCancelSession);
            btnEndSession = (Button) itemView.findViewById(R.id.btnEndSession);
            layoutBtn = (LinearLayout) itemView.findViewById(R.id.layoutBtn);
            layoutStartedStatus = (LinearLayout) itemView.findViewById(R.id.layoutStartedStatus);
            textStartedStatus = (TextView) itemView.findViewById(R.id.textStartedStatus);
        }
    }

    private void startSession(final Session dataItem, final MyViewHolder viewHolder) {
        OkHttpClient httpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("id_session", dataItem.getId() + "")
                .build();
        Request httpRequest = new Request.Builder()
                .url(App.URL_POST_SESSION_START)
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
                if (response.isSuccessful() && response.code() == 200) {
                    String json = response.body().string();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(json);
                        int success = jsonObject.getInt("success");
                        if(success == 1){
                            JSONObject dataReturn = jsonObject.getJSONObject("data");
                            String dateHeld = dataReturn.getString("date_held");
                            String startHourHeld = dataReturn.getString("start_hour_held");
                            dataItem.setDateHeld(dateHeld);
                            dataItem.setStartHourHeld(startHourHeld);
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.dismiss();
                                    viewHolder.layoutBtn.setVisibility(View.GONE);
                                    viewHolder.layoutStartedStatus.setVisibility(View.VISIBLE);
                                    viewHolder.textStartedStatus.setText("Sesi belajar telah dimulai. Tap tombol \""+mContext.getString(R.string.action_end_session)+"\" untuk mengakhiri sesi");
                                    viewHolder.btnStartSession.setVisibility(View.GONE);
                                    viewHolder.btnEndSession.setVisibility(View.VISIBLE);
                                }
                            });
                        }else{
                            String message = jsonObject.getString("message");
                            new RunnableDialogMessage(mContext, "", message, mProgressDialog);
                        }
                    } catch (JSONException e) {
                        new RunnableDialogMessage(mContext, "", e.getMessage(), mProgressDialog);
                    }
                } else {
                    new RunnableDialogMessage(mContext, "", response.message(), mProgressDialog);
                }
            }
        });
    }

    private void endSession(final Session dataSession) {
        OkHttpClient httpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("id_session", dataSession.getId()+"")
                .build();
        Request httpRequest = new Request.Builder()
                .url(App.URL_POST_SESSION_END)
                .post(formBody)
                .build();
        Call httpCall = httpClient.newCall(httpRequest);

        mProgressDialog.setMessage("Mengakhiri sesi...");
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
                        int success = jsonObject.getInt("success");
                        if(success == 1){
                            JSONObject dataReturn = jsonObject.getJSONObject("data");
                            String endHourHeld = dataReturn.getString("end_hour_held");
                            dataSession.setEndHourHeld(endHourHeld);
                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.dismiss();

                                    Bundle params = new Bundle();
                                    params.putInt("id_session", dataSession.getId());
                                    params.putString(Constants.PARAM_KEY_NAME, dataSession.getUser().getName());
                                    params.putString(Constants.PARAM_KEY_PHOTO_URL, dataSession.getUser().getPhotoUrl());
                                    params.putString(Constants.PARAM_KEY_SUBJECT_NAME, dataSession.getSubject());
                                    params.putString("date_held", dataSession.getDateHeld());
                                    params.putString("start_hour_held", dataSession.getStartHourHeld());
                                    params.putString("end_hour_held", dataSession.getEndHourHeld());

                                    Intent rateIntent = new Intent(mContext, RateTutorActivity.class);
                                    rateIntent.putExtras(params);

                                    mContext.startActivity(rateIntent);
                                    ((MainActivity)mContext).finish();
                                }
                            });
                        }else{
                            String message = jsonObject.getString("message");
                            new RunnableDialogMessage(mContext, "", message, mProgressDialog);
                        }
                        final String message = jsonObject.getString("message");
                    } catch (JSONException e) {
                        new RunnableDialogMessage(mContext, "", e.getMessage(), mProgressDialog);
                    }
                }else{
                    new RunnableDialogMessage(mContext, "", response.message(), mProgressDialog);
                }
            }
        });
    }

    private void cancelSession(final Session dataItem, MyViewHolder viewHolder, final int dataIndex) {
        OkHttpClient httpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("id_session", dataItem.getId() + "")
                .add("user_context", mUserContextAs + "")
                .build();
        Request httpRequest = new Request.Builder()
                .url(App.URL_POST_SESSION_CANCEL)
                .post(formBody)
                .build();
        Call httpCall = httpClient.newCall(httpRequest);

        mProgressDialog.setMessage("Membatalkan sesi...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.code() == 200) {
                    String json = response.body().string();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(json);
                        int success = jsonObject.getInt("success");
                        if(success == 1){
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.dismiss();
                                    String dialogTitle = "Jadwal sesi berhasil dibatalkan";
                                    App.showSimpleDialog(mContext, dialogTitle);
                                    mSessionList.remove(dataIndex);
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        new RunnableDialogMessage(mContext, "", e.getMessage(), mProgressDialog);
                    }
                } else {
                    new RunnableDialogMessage(mContext, "", response.message(), mProgressDialog);
                }
            }
        });
    }
}
