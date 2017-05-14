package com.widiarifki.findtutor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.widiarifki.findtutor.helper.SessionManager;
import com.widiarifki.findtutor.model.User;

public class WelcomeActivity extends AppCompatActivity {

    SessionManager session;
    User userLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        General.initStartIntent(WelcomeActivity.this);

        /*session = new SessionManager(getApplicationContext());
        userLogin = session.getUserDetail();
        if (session.isLoggedIn()) {
            Intent intent;
            // User is already logged in. Take him to main activity or complete profile
            if(userLogin.getIsProfileComplete() == 1){
                intent = new Intent(WelcomeActivity.this, General.homeActivity);
            }else{
                intent = new Intent(WelcomeActivity.this, General.profileActivity);
            }
            startActivity(intent);
            finish();
        }*/

        setContentView(R.layout.activity_welcome);

        Button mBtnRegister = (Button) findViewById(R.id.btnRegister);
        Button mBtnLogin = (Button) findViewById(R.id.btnLogin);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRegisterListener();
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLoginListener();
            }
        });
    }

    private void onClickRegisterListener() {
        Intent intent = new Intent(this, RegisterActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, RegisterActivity.REQUEST_CODE);
    }

    private void onClickLoginListener() {
        Intent intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, LoginActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RegisterActivity.REQUEST_CODE || resultCode==LoginActivity.REQUEST_CODE){
            finish();
        }
    }
}
