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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.helper.CircleTransform;
import com.widiarifki.findtutor.helper.RunnableDialogMessage;
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

public class SessionAcceptedAdapter extends RecyclerView.Adapter {

    Context mContext;
    List<Session> mSessionList;
    int mUserContextAs;
    ProgressDialog mProgressDialog;

    public SessionAcceptedAdapter(Context context, List<Session> sessionList) {
        mContext = context;
        mSessionList = sessionList;
        mProgressDialog = new ProgressDialog(mContext);
    }

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Session dataItem = mSessionList.get(position);
        final int idSession = dataItem.getId();
        final String name = dataItem.getUser().getName();
        String subject = dataItem.getSubject();
        String schedule = dataItem.getScheduleDate() + " " + dataItem.getStartHour() + " " + dataItem.getEndHour();
        String location = dataItem.getLocationAddress() + " (" + dataItem.getDistanceBetween() + " km)";
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
        viewHolder.tvLocation.setText(location);
        if(mUserContextAs == Constants.SESSION_CONTEXT_AS_TUTOR){
            viewHolder.btnStart.setVisibility(View.GONE);
        }else{
            viewHolder.btnStart.setVisibility(View.VISIBLE);
        }
        viewHolder.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.confirm_message_start_session) + " " + name + "?")
                        .setNegativeButton(mContext.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(mContext.getString(R.string.action_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startSession(idSession, viewHolder);
                            }
                        });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });
        viewHolder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if(status == Constants.SESSION_STARTED){
            viewHolder.layoutBtn.setVisibility(View.GONE);
            viewHolder.layoutInfoStarted.setVisibility(View.VISIBLE);
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
        public TextView tvLocation;
        public Button btnStart;
        public Button btnCancel;
        public LinearLayout layoutBtn;
        public LinearLayout layoutInfoStarted;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgvUserPhoto = (ImageView) itemView.findViewById(R.id.imgvUserPhoto);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvSubjectName = (TextView) itemView.findViewById(R.id.tvSubjectName);
            tvSchedule = (TextView) itemView.findViewById(R.id.tvSchedule);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            btnStart = (Button) itemView.findViewById(R.id.btnStart);
            btnCancel = (Button) itemView.findViewById(R.id.btnCancel);
            layoutBtn = (LinearLayout) itemView.findViewById(R.id.layoutBtn);
            layoutInfoStarted = (LinearLayout) itemView.findViewById(R.id.layoutInfoStarted);
        }
    }

    private void startSession(int idSession, final MyViewHolder viewHolder) {
        OkHttpClient httpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("id_session", idSession+"")
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
                if(response.isSuccessful() && response.code() == 200){
                    String json = response.body().string();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(json);
                        final int success = jsonObject.getInt("success");
                        final String message = jsonObject.getString("message");
                        ((Activity)mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(success == 1){
                                    mProgressDialog.dismiss();
                                    viewHolder.layoutBtn.setVisibility(View.GONE);
                                    viewHolder.layoutInfoStarted.setVisibility(View.VISIBLE);
                                }else{
                                    new RunnableDialogMessage(mContext, "", message, mProgressDialog);
                                }
                            }
                        });
                    } catch (JSONException e) {
                        new RunnableDialogMessage(mContext, "", e.getMessage(), mProgressDialog);
                    }
                }else{
                    new RunnableDialogMessage(mContext, "", response.message(), mProgressDialog);
                }
            }
        });
    }
}
