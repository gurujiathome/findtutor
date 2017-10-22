package com.widiarifki.findtutor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RateTutorActivity extends AppCompatActivity {

    int mIdSession;
    String mTutorName;
    String mTutorPhotoUrl;
    String mSubjectName;
    String mDateHeld;
    String mStartHourHeld;
    String mEndHourHeld;

    ImageView imgvUserPhoto;
    TextView tvName;
    TextView tvSubject;
    TextView tvDate;
    TextView tvTime;
    RatingBar ratingKnowledge;
    RatingBar ratingAttitude;
    RatingBar ratingClarity;
    RatingBar ratingHelpfulness;
    EditText inputComment;
    private EditText inputFee;
    Button btnSend;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        mIdSession = extras.getInt("id_session", 0);
        mTutorName = extras.getString(Constants.PARAM_KEY_NAME, null);
        mTutorPhotoUrl = extras.getString(Constants.PARAM_KEY_PHOTO_URL, null);
        mSubjectName = extras.getString(Constants.PARAM_KEY_SUBJECT_NAME, null);
        mDateHeld = extras.getString("date_held", null);
        mStartHourHeld = extras.getString("start_hour_held", null);
        mEndHourHeld = extras.getString("end_hour_held", null);

        // Set View
        setContentView(R.layout.activity_rate_tutor);
        mProgressDialog = new ProgressDialog(this);

        imgvUserPhoto = (ImageView) findViewById(R.id.imgvUserPhoto);
        tvName = (TextView) findViewById(R.id.tvName);
        tvSubject = (TextView) findViewById(R.id.tvSubject);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        ratingAttitude = (RatingBar) findViewById(R.id.ratingAttitude);
        ratingClarity = (RatingBar) findViewById(R.id.ratingClarity);
        ratingHelpfulness = (RatingBar) findViewById(R.id.ratingHelpfulness);
        ratingKnowledge = (RatingBar) findViewById(R.id.ratingKnowledge);
        inputComment = (EditText) findViewById(R.id.inputComment);
        inputFee = (EditText) findViewById(R.id.inputFee);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });

        tvName.setText(mTutorName);
        tvSubject.setText(mSubjectName);
        tvDate.setText(mDateHeld);
        tvTime.setText(mStartHourHeld + " " + mEndHourHeld);
    }

    private void sendFeedback() {
        OkHttpClient httpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("id_session", mIdSession+"")
                .add(Constants.PARAM_KEY_RATE_ATTITUDE, String.valueOf(ratingAttitude.getRating()))
                .add(Constants.PARAM_KEY_RATE_KNOWLEDGE, String.valueOf(ratingKnowledge.getRating()))
                .add(Constants.PARAM_KEY_RATE_HELPFULNESS, String.valueOf(ratingHelpfulness.getRating()))
                .add(Constants.PARAM_KEY_RATE_CLARITY, String.valueOf(ratingClarity.getRating()))
                .add(Constants.PARAM_KEY_STUDENT_FEEDBACK, inputComment.getText().toString())
                .add("tutor_fee", inputFee.getText().toString())
                .build();
        Request httpRequest = new Request.Builder()
                .url(App.URL_POST_STUDENT_FEEDBACK)
                .post(formBody)
                .build();
        Call httpCall = httpClient.newCall(httpRequest);

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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.dismiss();

                                    AlertDialog dialog = new AlertDialog.Builder(RateTutorActivity.this)
                                            .setCancelable(false)
                                            .setMessage("Terima kasih atas feedback anda")
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(RateTutorActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
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
}
