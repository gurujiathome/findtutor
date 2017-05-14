package com.widiarifki.findtutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.widiarifki.findtutor.helper.SessionManager;
import com.widiarifki.findtutor.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileFormActivity extends AppCompatActivity implements VerticalStepperForm {

    private VerticalStepperFormLayout verticalStepperForm;

    // Input Elements
    EditText inputName;
    EditText inputPhone;
    RadioGroup rgrupUserOpt;
    RadioButton rbtnIsTutor;
    RadioButton rbtnIsStudent;
    RadioButton rbtnIsBoth;
    LinearLayout layoutUserPhoto;
    Button btnPhotoCamera;
    Button btnPhotoAlbum;
    ImageView imageUserPhoto;

    ProgressDialog pDialog;

    SessionManager session;
    User userLogin;
    Uri mPhotoUri;
    Uri mSavedPhotoUri;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_form);

        mSavedPhotoUri = null;
        // retrieve session
        session = new SessionManager(getApplicationContext());
        userLogin = session.getUserDetail();

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        // Stepper form element
        String[] mySteps = {"Nama Lengkap", "No Handphone", "Tujuan Bergabung", "Upload Foto Profil"/*, "Foto Kartu Identitas"*/};

        // Finding the view
        verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);

        // Setting up and initializing the form
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, mySteps, this, this)
                .displayBottomNavigation(true) // It is true by default, so in this case this line is not necessary
                .init();
    }

    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        switch (stepNumber) {
            case 0:
                view = createNameStep();
                break;
            case 1:
                view = createPhoneStep();
                break;
            case 2:
                view = createChooseUserStep();
                break;
            case 3:
                view = createPhotoStep();
                break;
        }
        return view;
    }

    private View createNameStep() {
        // Here we generate programmatically the view that will be added by the system to the step content layoutUserPhoto
        inputName = new EditText(this);
        inputName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        inputName.setSingleLine(true);
        inputName.setHint(getString(R.string.prompt_fullname));
        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkName();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        return inputName;
    }

    private View createPhoneStep() {
        inputPhone = new EditText(this);
        inputPhone.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        inputPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        inputPhone.setSingleLine(true);
        inputPhone.setHint(getString(R.string.prompt_phone));
        inputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkPhone();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        return inputPhone;
    }

    private View createChooseUserStep(){
        rgrupUserOpt = new RadioGroup(this);

        rbtnIsTutor = new RadioButton(this);
        rbtnIsTutor.setId(R.id.radio_opt_is_tutor);
        rbtnIsTutor.setText(R.string.label_opt_is_tutor);
        rgrupUserOpt.addView(rbtnIsTutor);

        rbtnIsStudent = new RadioButton(this);
        rbtnIsStudent.setId(R.id.radio_opt_is_student);
        rbtnIsStudent.setText(R.string.label_opt_is_student);
        rgrupUserOpt.addView(rbtnIsStudent);

        rbtnIsBoth = new RadioButton(this);
        rbtnIsBoth.setId(R.id.radio_opt_is_both);
        rbtnIsBoth.setText(R.string.label_opt_is_both);
        rgrupUserOpt.addView(rbtnIsBoth);

        rgrupUserOpt.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                checkUserOpt();
            }
        });
        return rgrupUserOpt;
    }

    private View createPhotoStep(){
        layoutUserPhoto = new LinearLayout(this);
        layoutUserPhoto.setOrientation(LinearLayout.VERTICAL);

        LinearLayout layoutBtn = new LinearLayout(this);
        layoutBtn.setOrientation(LinearLayout.HORIZONTAL);

        btnPhotoCamera = new Button(this);
        btnPhotoCamera.setText(getString(R.string.label_pick_selfie));
        btnPhotoCamera.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_camera,0, 0, 0);
        btnPhotoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        layoutBtn.addView(btnPhotoCamera);

        btnPhotoAlbum = new Button(this);
        btnPhotoAlbum.setText(getString(R.string.label_pick_photo));
        btnPhotoAlbum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_gallery,0, 0, 0);
        btnPhotoAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPicture();
            }
        });
        layoutBtn.addView(btnPhotoAlbum);

        layoutUserPhoto.addView(layoutBtn);

        imageUserPhoto = new ImageView(this);
        imageUserPhoto.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutUserPhoto.addView(imageUserPhoto);

        return layoutUserPhoto;
    }

    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case 0:
                checkName();
                break;
            case 1:
                checkPhone();
                break;
            case 2:
                checkUserOpt();
                break;
            case 3:
                checkUserPhoto();
                break;
        }
    }

    private void checkName() {
        if(inputName.getText().length() > 0) {
            verticalStepperForm.setActiveStepAsCompleted();
        } else {
            // This error message is optional (use null if you don't want to display an error message)
            String errorMessage = getString(R.string.error_field_required);
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }
    }

    private void checkPhone() {
        if(inputPhone.getText().length() > 0) {
            verticalStepperForm.setActiveStepAsCompleted();
        } else {
            // This error message is optional (use null if you don't want to display an error message)
            String errorMessage = getString(R.string.error_field_required);
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }
    }

    private void checkUserOpt(){
        int selectedUserType = rgrupUserOpt.getCheckedRadioButtonId();
        if(selectedUserType < 0){ // means no opt selected
            String errorMessage = getString(R.string.error_choose_user_type);
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }else{
            verticalStepperForm.setActiveStepAsCompleted();
        }
    }

    void checkUserPhoto(){
        if(mSavedPhotoUri == null){
            String errorMessage = getString(R.string.error_field_required);
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }else{
            verticalStepperForm.setActiveStepAsCompleted();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = userLogin.getEmail();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    void takePicture(){
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //resolveActivity(), returns the first activity component that can handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mPhotoUri = FileProvider.getUriForFile(ProfileFormActivity.this, "com.widiarifki.findtutor", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }else{
                Toast.makeText(getApplication(), "Aplikasi kamera tidak ditemukan", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(mPhotoUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mSavedPhotoUri = result.getUri();
                // show cropped image to form activity
                imageUserPhoto.setImageURI(mSavedPhotoUri);
                checkUserPhoto();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    void uploadPhoto(){
        MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
        OkHttpClient client = new OkHttpClient();
        RequestBody req = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id_user", userLogin.getId()+"")
                .addFormDataPart("image", userLogin.getId()+".jpg", RequestBody.create(MEDIA_TYPE_JPG, new File(mSavedPhotoUri.getPath())) )
                .build();
        Request request = new Request.Builder()
                .url(General.urlUserUploadPhoto)
                .post(req)
                .build();
        Call httpCall = client.newCall(request);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //System.out.println(response.body().string());
            }
        });

        return;
    }

    void pickPicture(){

    }

    @Override
    public void sendData() {
        pDialog.setMessage("Mengirimkan data...");
        if(!pDialog.isShowing()) pDialog.show();

        // get value from fields
        String name = inputName.getText().toString();
        String phone = inputPhone.getText().toString();
        int is_tutor = 0;
        int is_student = 0;
        int selectedUserType = rgrupUserOpt.getCheckedRadioButtonId();

        if(selectedUserType == rbtnIsTutor.getId()) is_tutor = 1;
        else if(selectedUserType == rbtnIsStudent.getId()) is_student = 1;
        else if(selectedUserType == rbtnIsBoth.getId()){
            is_tutor = 1;
            is_student = 1;
        }

        MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");

        OkHttpClient httpClient = new OkHttpClient();
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id_user", userLogin.getId()+"")
                .addFormDataPart("name", name)
                .addFormDataPart("phone", phone)
                .addFormDataPart("is_tutor", is_tutor+"")
                .addFormDataPart("is_student", is_student+"")
                .addFormDataPart("user_photo", userLogin.getId()+".jpg", RequestBody.create(MEDIA_TYPE_JPG, new File(mSavedPhotoUri.getPath())) )
                .build();

        Request httpRequest = new Request.Builder()
                .url(General.urlUserCompleteForm)
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
                            session.updateSession(userLogin);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Toast.makeText(getApplicationContext(), "Submit profil berhasil", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ProfileFormActivity.this, General.homeActivity);
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

    public class MyRunnable implements Runnable {
        String message;
        public MyRunnable(String message) {
            this.message = message;
        }

        public void run() {
            General.showErrorDialog(ProfileFormActivity.this, "Submit Data Gagal", message);
        }
    }
}
