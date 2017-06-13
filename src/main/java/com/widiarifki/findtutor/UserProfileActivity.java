package com.widiarifki.findtutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.helper.RoundedCornersTransform;
import com.widiarifki.findtutor.helper.SessionManager;
import com.widiarifki.findtutor.model.User;

public class UserProfileActivity extends AppCompatActivity {

    SessionManager mSession;
    User mUserlogin;

    // UI comp.
    EditText mInputUserName;
    EditText mInputUserEmail;
    EditText mInputUserPhone;
    ImageView mImgUserPhoto;
    Button mBtnEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mSession = new SessionManager(getApplicationContext());
        mUserlogin = mSession.getUserDetail();

        // Bind UI comp.
        mInputUserName = (EditText) findViewById(R.id.input_name);
        mInputUserEmail = (EditText) findViewById(R.id.input_email);
        //tvUserPhone = (TextView) findViewById(R.id.text_user_profile_phone);
        mInputUserPhone = (EditText) findViewById(R.id.input_phone);
        mImgUserPhoto = (ImageView) findViewById(R.id.imgv_profile_photo);
        mBtnEditProfile = (Button) findViewById(R.id.btn_edit_profile);

        mInputUserName.setText(mUserlogin.getName());
        mInputUserEmail.setText(mUserlogin.getEmail());
        // tvUserPhone.setText(mUserLogin.getPhone());
        mInputUserPhone.setText(mUserlogin.getPhone());
        Picasso.with(UserProfileActivity.this).load(App.URL_PATH_PHOTO + mUserlogin.getPhotoUrl())
                .transform(new RoundedCornersTransform())
                //.transform(new CircleTransform())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.abc_btn_check_material).into(mImgUserPhoto);
        mBtnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, SettingProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
