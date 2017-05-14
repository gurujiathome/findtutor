package com.widiarifki.findtutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.widiarifki.findtutor.helper.SessionManager;
import com.widiarifki.findtutor.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = RegisterActivity.class.getSimpleName();
    SessionManager session;
    public static int REQUEST_CODE = 0;

    // UI references.
    private View viewForm;
    private AutoCompleteTextView inputEmail;
    private EditText inputPass;
    private EditText inputPassConf;
    private Button btnRegister;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this.getApplicationContext());

        setContentView(R.layout.activity_register);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        viewForm = findViewById(R.id.form);
        inputEmail = (AutoCompleteTextView) findViewById(R.id.input_email);
        inputPass = (EditText) findViewById(R.id.input_password);
        inputPassConf = (EditText) findViewById(R.id.input_password_conf);
        btnRegister = (Button) findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptRegister();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void attemptRegister() throws IOException {
        View focusView = null;
        boolean cancel = false;

        // Reset error
        inputEmail.setError(null);
        inputPass.setError(null);
        inputPassConf.setError(null);

        // Store values
        String email = inputEmail.getText().toString();
        String pass = inputPass.getText().toString();
        String passConf = inputPassConf.getText().toString();

        // check pass conf
        if(!TextUtils.isEmpty(pass) && !TextUtils.equals(pass, passConf)){
            inputPassConf.setError(getString(R.string.error_invalid_password_conf));
            focusView = inputPassConf;
            cancel = true;
        }

        // Check valid password
        if(TextUtils.isEmpty(pass)){
            inputPass.setError(getString(R.string.error_field_required));
            focusView = inputPass;
            cancel = true;
        }else if(!TextUtils.isEmpty(pass) && !isPasswordValid(pass)){
            inputPass.setError(getString(R.string.error_invalid_password));
            focusView = inputPass;
            cancel = true;
        }

        // Check email
        if(TextUtils.isEmpty(email)){
            inputEmail.setError(getString(R.string.error_field_required));
            focusView = inputEmail;
            cancel = true;
        }else if(!isEmailValid(email)){
            inputEmail.setError(getString(R.string.error_invalid_email));
            focusView = inputEmail;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else{
            pDialog.setMessage("Memproses registrasi akun...");
            if(!pDialog.isShowing()) pDialog.show();

            RequestBody formBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", pass)
                    .build();

            OkHttpClient httpClient = new OkHttpClient();

            Request httpRequest = new Request.Builder()
                    .url(General.urlUserRegister)
                    .post(formBody)
                    .build();

            Call httpCall = httpClient.newCall(httpRequest);
            httpCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //Log.v(TAG, String.valueOf(e));
                    // hide progress bar
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(pDialog.isShowing()) pDialog.hide();
                        }
                    });
                    // alert user
                    runOnUiThread(new MyRunnable(String.valueOf(e)));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(pDialog.isShowing()) pDialog.hide();
                        }
                    });
                    String json = response.body().string();
                    //Log.v(TAG, json);
                    if(response.isSuccessful()){
                        try {
                            JSONObject objResponse = new JSONObject(json);
                            int status = objResponse.getInt("success");
                            //Log.v(TAG, status+"");
                            if(status == 1){
                                // Retrieve user data from http response
                                String userData = objResponse.getString("data");
                                JSONObject objUserData = new JSONObject(userData);
                                // Store in Session w/ User object
                                User user = new User();
                                user.setId(objUserData.getInt("id_user"));
                                user.setEmail(objUserData.getString("email"));
                                user.setIsProfileComplete(objUserData.getInt("is_profile_complete"));
                                session.createSession(user);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                                        //Intent intent = new Intent(RegisterActivity.this, ProfileForm1Activity.class);
                                        //Intent intent = new Intent(RegisterActivity.this, General.homeActivity);
                                        Intent intent = new Intent(RegisterActivity.this, ProfileFormActivity.class);
                                        startActivity(intent);
                                        finish();
                                        //destroy parent activity
                                        //getParent().finish();
                                    }
                                });
                            }else if(status == 0){
                                int isEmailExist = objResponse.getInt("email_exist");
                                String message = objResponse.getString("error_msg");
                                // email sudah ada || error
                                if(isEmailExist == 1){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                        inputEmail.setError(getString(R.string.error_email_exist));
                                        inputEmail.requestFocus();
                                        }
                                    });
                                }else{
                                    runOnUiThread(new MyRunnable(message));
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private boolean isEmailValid(String email){
        return email.contains("@");
    }

    private boolean isPasswordValid(String password){
        return password.length() > 5;
    }

    @Override
    protected void onStop() {
        setResult(REQUEST_CODE);
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        setResult(REQUEST_CODE);
        super.onDestroy();
    }

    public class MyRunnable implements Runnable {
        String message;
        public MyRunnable(String message) {
            this.message = message;
        }

        public void run() {
            General.showErrorDialog(RegisterActivity.this, "Registrasi Gagal", message);
        }
    }
}
