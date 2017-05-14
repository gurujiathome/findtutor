package com.widiarifki.findtutor;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity /*implements LoaderCallbacks<Cursor>*/ {

    /**
     * Id to identity READ_CONTACTS permission request.

    private static final int REQUEST_READ_CONTACTS = 0;*/
    public static int REQUEST_CODE = 0;
    SessionManager session;

    // UI references.
    private AutoCompleteTextView inputEmail;
    private EditText inputPassword;
    private View viewForm;
    private Button btnLogin;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this.getApplicationContext());

        setContentView(R.layout.activity_login);
        setupActionBar();

        // Set up the login form.
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        viewForm = findViewById(R.id.login_form);
        inputEmail = (AutoCompleteTextView) findViewById(R.id.input_email);
        //populateAutoComplete();

        inputPassword = (EditText) findViewById(R.id.input_password);
        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /*private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }*/

    /**
     * Callback received when a permissions request has been completed.
     */
    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }*/

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
        inputEmail.setError(null);
        inputPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = inputEmail.getText().toString();
        String pass = inputPassword.getText().toString();

        // Check for a valid email address && required field.
        if(TextUtils.isEmpty(pass)){
            inputPassword.setError(getString(R.string.error_field_required));
            focusView = inputPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.error_field_required));
            focusView = inputEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            inputEmail.setError(getString(R.string.error_invalid_email));
            focusView = inputEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            pDialog.setMessage("Memproses login akun...");
            if(!pDialog.isShowing()) pDialog.show();

            /** Process Reg v.2**/
            RequestBody formBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", pass)
                    .build();

            OkHttpClient httpClient = new OkHttpClient();

            Request httpRequest = new Request.Builder()
                    .url(General.urlUserLogin)
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
                                // store di sqlite && session
                                // store session
                                String userData = objResponse.getString("data");
                                JSONObject objUserData = new JSONObject(userData);

                                User user = new User();
                                user.setId(objUserData.getInt("id_user"));
                                user.setEmail(objUserData.getString("email"));
                                user.setName(objUserData.getString("name"));
                                user.setPhone(objUserData.getString("phone"));
                                user.setIsTutor(objUserData.getInt("is_tutor"));
                                user.setIsStudent(objUserData.getInt("is_student"));
                                user.setIsProfileComplete(objUserData.getInt("is_profile_complete"));
                                session.createSession(user);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Selamat Datang!", Toast.LENGTH_SHORT).show();

                                        General.initStartIntent(LoginActivity.this);
                                        /*Intent intent;
                                        if(user.getIsProfileComplete() == 1){
                                            intent = new Intent(LoginActivity.this, General.homeActivity);
                                        }else{
                                            intent = new Intent(LoginActivity.this, General.profileActivity);
                                        }
                                        //Intent intent = new Intent(LoginActivity.this, General.homeActivity);
                                        startActivity(intent);
                                        finish();
                                        //destroy parent activity
                                        getParent().finish();*/
                                    }
                                });
                            }else if(status == 0){
                                int isWrongPass = objResponse.getInt("wrong_password");
                                String message = objResponse.getString("error_msg");
                                // email sudah ada || error
                                if(isWrongPass == 1){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            inputPassword.setError(getString(R.string.error_incorrect_password));
                                            inputPassword.requestFocus();
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

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    /*
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layoutUserPhoto.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }*/

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
            General.showErrorDialog(LoginActivity.this, "Login Gagal", message);
        }
    }
}

