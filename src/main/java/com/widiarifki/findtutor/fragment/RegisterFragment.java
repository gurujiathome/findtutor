package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.widiarifki.findtutor.CompleteProfileActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.helper.DialogMessage;
import com.widiarifki.findtutor.helper.SessionManager;
import com.widiarifki.findtutor.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by widiarifki on 27/05/2017.
 */

public class RegisterFragment extends Fragment {

    private Context mContext;
    private Activity mContextActivity;
    private SessionManager mSession;

    // UI references.
    private AutoCompleteTextView mInputEmail;
    private EditText mInputPassword;
    private EditText mInputPassConf;
    private View mForm;
    private Button mBtnRegister;
    private ProgressDialog mProgressDialog;
    private String dialogTitle = "Registrasi Gagal";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        mContextActivity = (Activity)mContext;
        mSession = new SessionManager(mContext);
        User user = mSession.getUserDetail();

        View view = inflater.inflate(R.layout.fragment_welcome_register, container, false);

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);

        mForm = view.findViewById(R.id.form);
        mInputEmail = (AutoCompleteTextView) view.findViewById(R.id.input_email);
        mInputPassword = (EditText) view.findViewById(R.id.input_password);
        mInputPassConf = (EditText) view.findViewById(R.id.input_password_conf);
        mBtnRegister = (Button) view.findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptRegister();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    private void attemptRegister() throws IOException {
        View focusView = null;
        boolean cancel = false;

        // Reset error
        mInputEmail.setError(null);
        mInputPassword.setError(null);
        mInputPassConf.setError(null);

        // Store values
        String email = mInputEmail.getText().toString();
        String pass = mInputPassword.getText().toString();
        String passConf = mInputPassConf.getText().toString();

        // check pass conf
        if(!TextUtils.isEmpty(pass) && !TextUtils.equals(pass, passConf)){
            mInputPassConf.setError(getString(R.string.error_invalid_password_conf));
            focusView = mInputPassConf;
            cancel = true;
        }

        // Check valid password
        if(TextUtils.isEmpty(pass)){
            mInputPassword.setError(getString(R.string.error_field_required));
            focusView = mInputPassword;
            cancel = true;
        }else if(!TextUtils.isEmpty(pass) && !isPasswordValid(pass)){
            mInputPassword.setError(getString(R.string.error_invalid_password));
            focusView = mInputPassword;
            cancel = true;
        }

        // Check email
        if(TextUtils.isEmpty(email)){
            mInputEmail.setError(getString(R.string.error_field_required));
            focusView = mInputEmail;
            cancel = true;
        }else if(!isEmailValid(email)){
            mInputEmail.setError(getString(R.string.error_invalid_email));
            focusView = mInputEmail;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else{
            mProgressDialog.setMessage("Memproses registrasi akun...");
            if(!mProgressDialog.isShowing()) mProgressDialog.show();

            RequestBody formBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", pass)
                    .build();

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();;

            Request httpRequest = new Request.Builder()
                    .url(App.URL_USER_REGISTER)
                    .post(formBody)
                    .build();

            Call httpCall = httpClient.newCall(httpRequest);
            httpCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //Log.v(TAG, String.valueOf(e));
                    // alert user
                    getActivity().runOnUiThread(new DialogMessage(mContext, dialogTitle, String.valueOf(e), mProgressDialog));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mProgressDialog.isShowing()) mProgressDialog.hide();
                        }
                    });
                    String json = response.body().string();
                    //Log.v(TAG, json);
                    if(response.isSuccessful() && response.code() == 200){
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
                                user.setId(objUserData.getInt(mSession.KEY_ID_USER));
                                user.setEmail(objUserData.getString(mSession.KEY_EMAIL));
                                user.setIsProfileComplete(objUserData.getInt(mSession.KEY_IS_PROFILE_COMPLETE));
                                mSession.updateSession(user);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(mContext, CompleteProfileActivity.class);
                                        startActivity(intent);
                                        mContextActivity.finish();
                                    }
                                });
                            }else if(status == 0){
                                int isEmailExist = objResponse.getInt("email_exist");
                                String message = objResponse.getString("error_msg");
                                // email sudah ada || error
                                if(isEmailExist == 1){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mInputEmail.setError(getString(R.string.error_email_exist));
                                            mInputEmail.requestFocus();
                                        }
                                    });
                                }else{
                                    getActivity().runOnUiThread(new DialogMessage(mContext, dialogTitle, message, mProgressDialog));
                                }

                            }
                        } catch (JSONException e) {
                            // alert user
                            getActivity().runOnUiThread(new DialogMessage(mContext, dialogTitle, e.getMessage(), mProgressDialog));
                        }
                    }else{
                        // alert user
                        getActivity().runOnUiThread(new DialogMessage(mContext, dialogTitle, response.message(), mProgressDialog));
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
}