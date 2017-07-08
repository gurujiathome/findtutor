package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.helper.RunnableDialogMessage;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by widiarifki on 05/06/2017.
 */

public class SettingAccountFragment extends Fragment {

    private Context mContext;
    private Activity mContextActivity;

    SessionManager mSession;
    User mUserLogin;

    // UI comp.
    LinearLayout mFormLayout;
    EditText mInputEmail;
    Button mBtnSave;

    ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        View view = inflater.inflate(R.layout.fragment_setting_account, container, false);

        mSession = new SessionManager(mContext);
        mUserLogin = mSession.getUserDetail();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);

        // Bind UI comp.
        mFormLayout = (LinearLayout) view.findViewById(R.id.form_layout);
        mInputEmail = (EditText) view.findViewById(R.id.input_email);

        bindInitialData();

        return view;
    }

    void bindInitialData(){
        mUserLogin = mSession.getUserDetail();
        mInputEmail.setText(mUserLogin.getEmail());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void saveChanges(){
        final String dialogTitle = "Submit Data Gagal";
        // Store values
        String email = mInputEmail.getText().toString();

        // Pass validation
        mProgressDialog.setMessage("Menyimpan perubahan...");
        if(!mProgressDialog.isShowing()) mProgressDialog.show();

        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id_user", mUserLogin.getId()+"")
                .addFormDataPart("email", email)
                .build();

        OkHttpClient httpClient = new OkHttpClient();

        Request httpRequest = new Request.Builder()
                .url(App.URL_EDIT_PROFILE)
                .post(formBody)
                .build();

        Call httpCall = httpClient.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // alert user
                getActivity().runOnUiThread(new RunnableDialogMessage(mContext, dialogTitle, String.valueOf(e), mProgressDialog));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                if(response.isSuccessful() && response.code() == 200){
                    try {
                        JSONObject responseObj = new JSONObject(responseStr);
                        int status = responseObj.getInt("success");
                        //Log.v(TAG, status+"");
                        if(status == 1){
                            // Retrieve user data from http response
                            String userData = responseObj.getString("data");
                            JSONObject objUserData = new JSONObject(userData);

                            // Store in Session w/ User object
                            mUserLogin.setEmail(objUserData.getString(mSession.KEY_EMAIL));
                            mSession.updateSession(mUserLogin);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
                                    Toast.makeText(mContext, "Ganti email akun berhasil", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            String message = responseObj.getString("error_msg");
                            // alert user
                            getActivity().runOnUiThread(new RunnableDialogMessage(mContext, dialogTitle, message, mProgressDialog));
                        }
                    } catch (JSONException e) {
                        // alert user
                        getActivity().runOnUiThread(new RunnableDialogMessage(mContext, dialogTitle, e.getMessage(), mProgressDialog));
                    }
                }else{
                    // alert user
                    getActivity().runOnUiThread(new RunnableDialogMessage(mContext, dialogTitle, response.message(), mProgressDialog));
                }
            }
        });
    }
}
