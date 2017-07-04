package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.helper.RunnableDialogMessage;
import com.widiarifki.findtutor.helper.FormInputChecker;
import com.widiarifki.findtutor.helper.SessionManager;
import com.widiarifki.findtutor.model.AvailabilityPerDay;
import com.widiarifki.findtutor.model.Education;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class LoginFragment extends Fragment {

    private Context mContext;
    private Activity mContextActivity;
    private SessionManager mSession;

    // UI references.
    private AutoCompleteTextView mInputEmail;
    private EditText mInputPassword;
    private View mForm;
    private Button mBtnLogin;
    private ProgressDialog mProgressDialog;
    private String dialogTitle = "Login Gagal";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        mContextActivity = (Activity)mContext;
        mSession = new SessionManager(mContext);

        View view = inflater.inflate(R.layout.fragment_welcome_login, container, false);

        // Set up the login form.
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);

        mForm = view.findViewById(R.id.login_form);
        mInputEmail = (AutoCompleteTextView) view.findViewById(R.id.input_email);
        //populateAutoComplete();
        mInputPassword = (EditText) view.findViewById(R.id.input_password);
        mInputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mBtnLogin = (Button) view.findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        return view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        boolean cancel = false;
        View focusView = null;

        // Reset errors.
        mInputEmail.setError(null);
        mInputPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mInputEmail.getText().toString();
        String pass = mInputPassword.getText().toString();

        // Check for a valid email address && required field.
        if(TextUtils.isEmpty(pass)){
            mInputPassword.setError(getString(R.string.error_field_required));
            focusView = mInputPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mInputEmail.setError(getString(R.string.error_field_required));
            focusView = mInputEmail;
            cancel = true;
        } else if (!FormInputChecker.isEmailValid(email)) {
            mInputEmail.setError(getString(R.string.error_invalid_email));
            focusView = mInputEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mProgressDialog.setMessage("Memproses login akun...");
            if(!mProgressDialog.isShowing()) mProgressDialog.show();

            /** Process Reg v.2**/
            RequestBody formBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", pass)
                    .build();

            OkHttpClient httpClient = new OkHttpClient();

            Request httpRequest = new Request.Builder()
                    .url(App.URL_LOGIN)
                    .post(formBody)
                    .build();

            Call httpCall = httpClient.newCall(httpRequest);
            httpCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //Log.v(TAG, String.valueOf(e));
                    // alert user
                    getActivity().runOnUiThread(new RunnableDialogMessage(mContext, dialogTitle, String.valueOf(e), mProgressDialog));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
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
                                // store di sqlite && session
                                // store session
                                String userData = objResponse.getString("data");
                                JSONObject objUserData = new JSONObject(userData);

                                User user = new User();
                                user.setId(objUserData.getInt(mSession.KEY_ID_USER));
                                user.setEmail(objUserData.getString(mSession.KEY_EMAIL));
                                user.setName(objUserData.getString(mSession.KEY_NAME));
                                user.setGender(objUserData.getInt(mSession.KEY_GENDER));
                                user.setPhone(objUserData.getString(mSession.KEY_PHONE));
                                user.setPhotoUrl(objUserData.getString(mSession.KEY_PHOTO_URL));
                                user.setIsTutor(objUserData.getInt(mSession.KEY_IS_TUTOR));
                                user.setIsStudent(objUserData.getInt(mSession.KEY_IS_STUDENT));
                                user.setLatitude(objUserData.getString(mSession.KEY_LATITUDE));
                                user.setLongitude(objUserData.getString(mSession.KEY_LONGITUDE));
                                user.setLocationAddress(objUserData.getString(mSession.KEY_LOCATION_ADDRESS));
                                user.setDeviceId(objUserData.getString(mSession.KEY_DEVICE_ID));
                                user.setIsProfileComplete(objUserData.getInt(mSession.KEY_IS_PROFILE_COMPLETE));
                                user.setIsAvailable(objUserData.getInt(mSession.KEY_IS_AVAILABLE));
                                if(objUserData.getInt(mSession.KEY_IS_TUTOR) == 1) {
                                    user.setBio(objUserData.getString(mSession.KEY_BIO));
                                    user.setMinPriceRate(objUserData.getInt(mSession.KEY_MIN_PRICE_RATE));
                                    user.setMaxTravelDistance(objUserData.getInt(mSession.KEY_MAX_TRAVEL_DISTANCE));
                                    if (objUserData.getString(mSession.KEY_EDUCATIONS) != null) {
                                        Type type = new TypeToken<List<Education>>() {}.getType();
                                        List<Education> educationList = new Gson().fromJson(objUserData.getString(mSession.KEY_EDUCATIONS), type);
                                        user.setEducations(educationList);
                                    }

                                    if(objUserData.getString(mSession.KEY_SUBJECTS) != null) {
                                        Type subjectType = new TypeToken<Map<String, SavedSubject>>() {}.getType();
                                        Map<String, SavedSubject> subjectMap = new Gson().fromJson(objUserData.getString(mSession.KEY_SUBJECTS), subjectType);
                                        if (subjectMap != null) {
                                            HashMap<String, SavedSubject> subjectHashmap = new HashMap<String, SavedSubject>(subjectMap); // cast process
                                            user.setSubjects(subjectHashmap);
                                        }
                                    }

                                    if(objUserData.getString(mSession.KEY_AVAILABILITIES) != null) {
                                        Type availabilityType = new TypeToken<Map<String, List<AvailabilityPerDay>>>() {}.getType();
                                        Map<String, List<AvailabilityPerDay>> availabilityMap = new Gson().fromJson(objUserData.getString(mSession.KEY_AVAILABILITIES), availabilityType);
                                        if (availabilityMap != null) {
                                            HashMap<String, List<AvailabilityPerDay>> availabilityHashmap = new HashMap<String, List<AvailabilityPerDay>>(availabilityMap); // cast process
                                            user.setAvailabilities(availabilityHashmap);
                                        }
                                    }
                                }
                                mSession.updateSession(user);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, "Selamat Datang!", Toast.LENGTH_SHORT).show();

                                        App.loadInitActivity(mContext);
                                    }
                                });
                            }else if(status == 0){
                                int isWrongPass = objResponse.getInt("wrong_password");
                                String message = objResponse.getString("error_msg");
                                // email sudah ada || error
                                if(isWrongPass == 1){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mInputPassword.setError(getString(R.string.error_incorrect_password));
                                            mInputPassword.requestFocus();
                                        }
                                    });
                                }else{
                                    getActivity().runOnUiThread(new RunnableDialogMessage(mContext, dialogTitle, message, mProgressDialog));
                                }

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
}
