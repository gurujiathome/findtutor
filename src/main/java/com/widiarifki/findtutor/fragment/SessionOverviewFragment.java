package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.helper.CircleTransform;
import com.widiarifki.findtutor.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by widiarifki on 25/07/2017.
 */

public class SessionOverviewFragment extends Fragment {

    private Context mContext;
    private Activity mContextActivity;
    private int mIdSession;
    private int mContextUser;
    private SessionManager mSessionManager;
    private User mUserLogin;

    ImageView imgvUserPhoto;
    TextView tvName;
    TextView tvSubject;
    TextView tvDate;
    TextView tvTime;
    TextView tvLabelPenilaian;
    RatingBar ratingKnowledge;
    RatingBar ratingAttitude;
    RatingBar ratingClarity;
    RatingBar ratingHelpfulness;
    TextView tvComment;
    Button btnTutorFeedback;
    Button btnComplaint;
    ProgressDialog mProgressDialog;
    private TextView tvFee;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle params = getArguments();
        mIdSession = params.getInt("id_session", 0);
        mContextUser = params.getInt("context_user", 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        mContextActivity = ((Activity)mContext);
        mSessionManager = new SessionManager(mContext);
        mUserLogin = mSessionManager.getUserDetail();

        mProgressDialog = new ProgressDialog(mContext);

        View view = inflater.inflate(R.layout.fragment_session_overview, container, false);

        imgvUserPhoto = (ImageView) view.findViewById(R.id.imgvUserPhoto);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvSubject = (TextView) view.findViewById(R.id.tvSubject);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvTime = (TextView) view.findViewById(R.id.tvTime);
        tvFee = (TextView) view.findViewById(R.id.tvFee);
        tvLabelPenilaian = (TextView) view.findViewById(R.id.tvLabelPenilaian);
        ratingAttitude = (RatingBar) view.findViewById(R.id.ratingAttitude);
        ratingClarity = (RatingBar) view.findViewById(R.id.ratingClarity);
        ratingHelpfulness = (RatingBar) view.findViewById(R.id.ratingHelpfulness);
        ratingKnowledge = (RatingBar) view.findViewById(R.id.ratingKnowledge);
        tvComment = (TextView) view.findViewById(R.id.tvComment);
        btnTutorFeedback = (Button) view.findViewById(R.id.btnTutorFeedback);
        if(mContextUser == Constants.SESSION_CONTEXT_AS_STUDENT)
            btnTutorFeedback.setVisibility(View.GONE);
        btnTutorFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_tutor_feedback, null);
                final EditText inputComment = (EditText) dialogView.findViewById(R.id.inputComment);
                final RatingBar ratingStudent = (RatingBar) dialogView.findViewById(R.id.ratingStudent);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Feedback untuk Siswa");
                builder.setView(dialogView);
                builder.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(getString(R.string.action_send), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OkHttpClient httpClient = new OkHttpClient();
                        FormBody formBody = new FormBody.Builder()
                                .add("id_session", mIdSession+"")
                                .add("tutor_rate", ratingStudent.getRating()+"")
                                .add("tutor_feedback", inputComment.getText().toString())
                                .build();
                        Request httpRequest = new Request.Builder()
                                .url(App.URL_POST_TUTOR_FEEDBACK)
                                .post(formBody)
                                .build();

                        Call httpCall = httpClient.newCall(httpRequest);
                        dialog.dismiss();
                        mProgressDialog.setMessage("Mengirimkan feedback...");
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
                                    try {
                                        JSONObject jsonObj = new JSONObject(json);
                                        int success = jsonObj.getInt("success");
                                        if(success == 1){
                                            mContextActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mProgressDialog.dismiss();

                                                    AlertDialog dialog = new AlertDialog.Builder(mContext)
                                                            .setCancelable(false)
                                                            .setMessage("Feedback telah disimpan")
                                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                }
                                                            })
                                                            .create();

                                                    dialog.show();
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        btnComplaint = (Button) view.findViewById(R.id.btnComplaint);
        btnComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_form_complaint, null);
                final EditText inputComplaint = (EditText) dialogView.findViewById(R.id.inputComplaint);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Ajukan Komplain");
                builder.setView(dialogView);
                builder.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton(getString(R.string.action_send), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OkHttpClient httpClient = new OkHttpClient();
                        FormBody formBody = new FormBody.Builder()
                                .add("id_session", mIdSession+"")
                                .add("id_user", mUserLogin.getId()+"")
                                .add("complaint", inputComplaint.getText().toString())
                                .build();
                        Request httpRequest = new Request.Builder()
                                .url(App.URL_POST_USER_COMPLAIN)
                                .post(formBody)
                                .build();

                        Call httpCall = httpClient.newCall(httpRequest);
                        dialog.dismiss();
                        mProgressDialog.setMessage("Mengirimkan feedback...");
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
                                    try {
                                        JSONObject jsonObj = new JSONObject(json);
                                        int success = jsonObj.getInt("success");
                                        if(success == 1){
                                            mContextActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mProgressDialog.dismiss();

                                                    AlertDialog dialog = new AlertDialog.Builder(mContext)
                                                            .setCancelable(false)
                                                            .setMessage("Pengaduan telah dikirimkan")
                                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                }
                                                            })
                                                            .create();

                                                    dialog.show();
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        downloadUserData();
        return view;
    }

    private void downloadUserData() {
        mProgressDialog.setMessage("Mengambil data..");
        mProgressDialog.show();

        OkHttpClient httpClient = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_GET_SESSION_OVERVIEW + "?id=" + mIdSession)
                .build();

        Call httpCall = httpClient.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful() && response.code() == 200) {
                    String json = response.body().string();
                    try {
                        final JSONObject jsonObj = new JSONObject(json);
                        if(jsonObj.getInt("success") == 1){
                            mContextActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject dataObj = jsonObj.getJSONObject("data");
                                        String urlPhoto = null;
                                        if(mContextUser == Constants.SESSION_CONTEXT_AS_STUDENT){
                                            urlPhoto = App.URL_PATH_PHOTO + dataObj.getInt("id_tutor") + ".jpg";
                                            tvName.setText(dataObj.getString("tutor"));
                                            tvLabelPenilaian.setText("Penilaian Anda");
                                        }else{
                                            urlPhoto = App.URL_PATH_PHOTO + dataObj.getInt("id_student") + ".jpg";
                                            tvName.setText(dataObj.getString("student"));
                                            tvLabelPenilaian.setText("Penilaian Siswa");
                                        }

                                        if(urlPhoto != null) {
                                            Picasso.with(mContext).load(urlPhoto)
                                                    .transform(new CircleTransform())
                                                    .placeholder(R.drawable.ic_person_black_24dp)
                                                    .error(R.drawable.ic_broken_image_black_24dp)
                                                    .into(imgvUserPhoto);
                                        }

                                        tvSubject.setText(dataObj.getString("subject"));
                                        tvDate.setText(dataObj.getString("date"));
                                        tvTime.setText(dataObj.getString("start_hour") + " - " + dataObj.get("end_hour") + "\n("+dataObj.getString("length")+")");
                                        tvFee.setText(dataObj.getString("tutor_fee"));
                                        tvFee.setText(dataObj.getString("tutor_fee"));
                                        ratingAttitude.setRating((float) dataObj.getDouble("rate_attitude"));
                                        ratingClarity.setRating((float) dataObj.getDouble("rate_clarity"));
                                        ratingHelpfulness.setRating((float) dataObj.getDouble("rate_helpfulness"));
                                        ratingKnowledge.setRating((float) dataObj.getDouble("rate_knowledge"));
                                        tvComment.setText(dataObj.getString("student_feedback"));

                                        mProgressDialog.dismiss();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
