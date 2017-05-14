package com.widiarifki.findtutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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

public class ProfileForm1Activity extends AppCompatActivity {

    SessionManager session;
    User userLogin;

    // UI reference
    EditText inputName;
    EditText inputPhone;
    Button btnNext;
    RadioGroup rgroupUserType;
    RadioButton rbtnIsTutor;
    RadioButton rbtnIsStudent;
    RadioButton rbtnIsBoth;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_form1);

        session = new SessionManager(this.getApplicationContext());
        userLogin = session.getUserDetail();

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        // Binding UI reference
        inputName = (EditText) findViewById(R.id.input_fullname);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        rgroupUserType = (RadioGroup) findViewById(R.id.radio_group_user_type);
        rbtnIsTutor = (RadioButton) findViewById(R.id.radio_opt_is_tutor);
        rbtnIsStudent = (RadioButton) findViewById(R.id.radio_opt_is_student);
        rbtnIsBoth = (RadioButton) findViewById(R.id.radio_opt_is_both);

        btnNext = (Button) findViewById(R.id.btn_next);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSubmit();
            }
        });
    }

    void attemptSubmit(){
        boolean cancel = false;
        View focusView = null;

        String name = inputName.getText().toString();
        String phone = inputPhone.getText().toString();
        int is_tutor = 0;
        int is_student = 0;
        int selectedUserType = rgroupUserType.getCheckedRadioButtonId();

        if(selectedUserType == rbtnIsTutor.getId()) is_tutor = 1;
        else if(selectedUserType == rbtnIsStudent.getId()) is_student = 1;
        else if(selectedUserType == rbtnIsBoth.getId()){
            is_tutor = 1;
            is_student = 1;
        }

        if(TextUtils.isEmpty(phone)){
            inputPhone.setError(getString(R.string.error_field_required));
            focusView = inputPhone;
            cancel = true;
        }

        if(TextUtils.isEmpty(name)){
            inputName.setError(getString(R.string.error_field_required));
            focusView = inputName;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else{
            // If edittext input has complete, validate one field more
            if(is_student == 0 && is_tutor == 0){
                General.showErrorDialog(ProfileForm1Activity.this, getString(R.string.error_complete_form), getString(R.string.error_choose_user_type));
            }else{
                pDialog.setMessage("Mengirimkan data...");
                if(!pDialog.isShowing()) pDialog.show();

                RequestBody formBody = new FormBody.Builder()
                        .add("id_user", userLogin.getId()+"")
                        .add("name", name)
                        .add("phone", phone)
                        .add("is_tutor", is_tutor+"")
                        .add("is_student", is_student+"")
                        .build();

                OkHttpClient httpClient = new OkHttpClient();

                Request httpRequest = new Request.Builder()
                        .url(General.urlUserCompleteForm1)
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
                        runOnUiThread(new ProfileForm1Activity.MyRunnable(String.valueOf(e)));
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
                                if(status == 1){
                                    // retrieve user data from http response
                                    String userData = objResponse.getString("data");
                                    JSONObject objUserData = new JSONObject(userData);
                                    // set another attribute's value
                                    userLogin.setName(objUserData.getString("name"));
                                    userLogin.setPhone(objUserData.getString("phone"));
                                    userLogin.setIsTutor(objUserData.getInt("is_tutor"));
                                    userLogin.setIsStudent(objUserData.getInt("is_student"));
                                    userLogin.setIsProfileComplete(objUserData.getInt("is_profile_complete"));

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Toast.makeText(getApplicationContext(), "Registrasi berhasil", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(ProfileForm1Activity.this, ProfileForm2Activity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }else{
                                    String message = objResponse.getString("error_msg");
                                    runOnUiThread(new MyRunnable(message));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }

    public class MyRunnable implements Runnable {
        String message;
        public MyRunnable(String message) {
            this.message = message;
        }

        public void run() {
            General.showErrorDialog(ProfileForm1Activity.this, "Submit Data Gagal", message);
        }
    }
}
