package com.widiarifki.findtutor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
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

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.helper.CircleTransform;
import com.widiarifki.findtutor.helper.RunnableDialogMessage;
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

public class CompleteProfileActivity extends AppCompatActivity implements VerticalStepperForm {

    private VerticalStepperFormLayout mVerticalStepperForm;
    private Context mContext = CompleteProfileActivity.this;
    private String dialogTitle = "Submit Data Gagal";
    // Input Elements
    EditText mInputName;
    EditText mInputPhone;
    RadioGroup mRgrupUserOpt;
    RadioButton mRbtnIsTutor;
    RadioButton mRbtnIsStudent;
    RadioButton mRbtnIsBoth;
    RadioGroup mRgrupGender;
    RadioButton mRbtnIsFemale;
    RadioButton mRbtnIsMale;
    LinearLayout mLayoutUserPhoto;
    Button mBtnOpenCamera;
    Button mBtnOpenAlbum;
    ImageView mImageUserPhoto;

    ProgressDialog mProgressDialog;

    SessionManager mSession;
    User mUserLogin;
    Uri mPhotoUri;
    Uri mSavedPhotoUri;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        mSavedPhotoUri = null;
        // retrieve mSession
        mSession = new SessionManager(getApplicationContext());
        mUserLogin = mSession.getUserDetail();

        mProgressDialog = new ProgressDialog(this);

        sayHello();

        // Stepper form element
        String[] mySteps = {"Nama Lengkap", "Jenis Kelamin", "No Handphone", "Bergabung Sebagai", "Upload Foto Profil"/*, "Foto Kartu Identitas"*/};

        // Finding the view
        mVerticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);

        // Setting up and initializing the form
        VerticalStepperFormLayout.Builder.newInstance(mVerticalStepperForm, mySteps, this, this)
                .displayBottomNavigation(true) // It is true by default, so in this case this line is not necessary
                .init();
    }

    public void sayHello() {
        App.showSimpleDialog(mContext, "Selamat Datang!", "Terima kasih telah bergabung di " +
                getString(R.string.app_name) +
                ". Selanjutnya, silakan lengkapi data diri anda.");
    }

    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        switch (stepNumber) {
            case 0:
                view = createNameStep();
                break;
            case 1:
                view = createChooseGender();
                break;
            case 2:
                view = createPhoneStep();
                break;
            case 3:
                view = createChooseUserStep();
                break;
            case 4:
                view = createPhotoStep();
                break;
        }
        return view;
    }

    private View createNameStep() {
        // Here we generate programmatically the view that will be added by the system to the step content mLayoutUserPhoto
        mInputName = new EditText(this);
        mInputName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mInputName.setSingleLine(true);
        mInputName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mInputName.setHint(getString(R.string.prompt_fullname));
        mInputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkName();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        return mInputName;
    }

    private View createPhoneStep() {
        mInputPhone = new EditText(this);
        mInputPhone.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mInputPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        mInputPhone.setSingleLine(true);
        mInputPhone.setHint(getString(R.string.prompt_phone));
        mInputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkPhone();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        return mInputPhone;
    }

    private View createChooseGender(){
        mRgrupGender = new RadioGroup(this);
        mRgrupGender.setOrientation(LinearLayout.HORIZONTAL);

        mRbtnIsMale = new RadioButton(this);
        mRbtnIsMale.setId(R.id.radio_opt_male);
        mRbtnIsMale.setText(R.string.label_opt_male);
        mRgrupGender.addView(mRbtnIsMale);

        mRbtnIsFemale = new RadioButton(this);
        mRbtnIsFemale.setId(R.id.radio_opt_female);
        mRbtnIsFemale.setText(R.string.label_opt_female);
        mRgrupGender.addView(mRbtnIsFemale);

        mRgrupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                checkGender();
            }
        });
        return mRgrupGender;
    }

    private View createChooseUserStep(){
        mRgrupUserOpt = new RadioGroup(this);

        mRbtnIsTutor = new RadioButton(this);
        mRbtnIsTutor.setId(R.id.radio_opt_is_tutor);
        mRbtnIsTutor.setText(R.string.label_opt_is_tutor);
        mRgrupUserOpt.addView(mRbtnIsTutor);

        mRbtnIsStudent = new RadioButton(this);
        mRbtnIsStudent.setId(R.id.radio_opt_is_student);
        mRbtnIsStudent.setText(R.string.label_opt_is_student);
        mRgrupUserOpt.addView(mRbtnIsStudent);

        mRbtnIsBoth = new RadioButton(this);
        mRbtnIsBoth.setId(R.id.radio_opt_is_both);
        mRbtnIsBoth.setText(R.string.label_opt_is_both);
        mRgrupUserOpt.addView(mRbtnIsBoth);

        mRgrupUserOpt.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                checkUserOpt();
            }
        });
        return mRgrupUserOpt;
    }

    private View createPhotoStep(){
        mLayoutUserPhoto = new LinearLayout(this);
        mLayoutUserPhoto.setOrientation(LinearLayout.VERTICAL);

        LinearLayout layoutBtn = new LinearLayout(this);
        layoutBtn.setOrientation(LinearLayout.HORIZONTAL);

        mBtnOpenCamera = new Button(this);
        mBtnOpenCamera.setText(getString(R.string.action_pick_selfie));
        mBtnOpenCamera.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_camera,0, 0, 0);
        mBtnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        layoutBtn.addView(mBtnOpenCamera);

        mBtnOpenAlbum = new Button(this);
        mBtnOpenAlbum.setText(getString(R.string.action_pick_from_gallery));
        mBtnOpenAlbum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_gallery,0, 0, 0);
        mBtnOpenAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPicture();
            }
        });
        layoutBtn.addView(mBtnOpenAlbum);

        mLayoutUserPhoto.addView(layoutBtn);

        mImageUserPhoto = new ImageView(this);
        mImageUserPhoto.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mImageUserPhoto.setAdjustViewBounds(true);
        mLayoutUserPhoto.addView(mImageUserPhoto);

        return mLayoutUserPhoto;
    }

    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case 0:
                checkName();
                break;
            case 1:
                checkGender();
                break;
            case 2:
                checkPhone();
                break;
            case 3:
                checkUserOpt();
                break;
            case 4:
                checkUserPhoto();
                break;
        }
    }

    private void checkName() {
        if(mInputName.getText().length() > 0) {
            mVerticalStepperForm.setActiveStepAsCompleted();
        } else {
            // This error message is optional (use null if you don't want to display an error message)
            String errorMessage = getString(R.string.error_field_required);
            mVerticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }
    }

    private void checkPhone() {
        if(mInputPhone.getText().length() > 0) {
            mVerticalStepperForm.setActiveStepAsCompleted();
        } else {
            // This error message is optional (use null if you don't want to display an error message)
            String errorMessage = getString(R.string.error_field_required);
            mVerticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }
    }

    private void checkGender(){
        int selectedUserType = mRgrupGender.getCheckedRadioButtonId();
        if(selectedUserType < 0){ // means no opt selected
            String errorMessage = getString(R.string.error_field_required_radio);
            mVerticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }else{
            mVerticalStepperForm.setActiveStepAsCompleted();
        }
    }

    private void checkUserOpt(){
        int selectedUserType = mRgrupUserOpt.getCheckedRadioButtonId();
        if(selectedUserType < 0){ // means no opt selected
            String errorMessage = getString(R.string.error_field_required_radio);
            mVerticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }else{
            mVerticalStepperForm.setActiveStepAsCompleted();
        }
    }

    void checkUserPhoto(){
        if(mSavedPhotoUri == null){
            String errorMessage = getString(R.string.error_field_required);
            mVerticalStepperForm.setActiveStepAsUncompleted(errorMessage);
        }else{
            mVerticalStepperForm.setActiveStepAsCompleted();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = mUserLogin.getEmail();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

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
                    mPhotoUri = FileProvider.getUriForFile(CompleteProfileActivity.this, mContext.getPackageName(), photoFile);
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

    void pickPicture(){
        CropImage.startPickImageActivity(CompleteProfileActivity.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(mPhotoUri)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setFixAspectRatio(true)
                    .setAllowFlipping(false)
                    .setActivityTitle("Tampilan Foto")
                    .start(this);
        }

        else if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            mPhotoUri = imageUri;
            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                CropImage.activity(mPhotoUri)
                        .setGuidelines(CropImageView.Guidelines.OFF)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setFixAspectRatio(true)
                        .setAllowFlipping(false)
                        .setActivityTitle("Tampilan Foto")
                        .start(this);
            }
        }

        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mSavedPhotoUri = result.getUri();
                Picasso.with(CompleteProfileActivity.this).load(mSavedPhotoUri)
                        .transform(new CircleTransform())
                        .into(mImageUserPhoto);
                checkUserPhoto();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void sendData() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle("Apakah anda yakin data sudah benar?");
        dialogBuilder.setMessage("Anda masih dapat menyunting data di menu pengaturan");
        dialogBuilder.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mVerticalStepperForm.setActiveStepAsCompleted();
            }
        });
        dialogBuilder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doSendData();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void doSendData() {
        // get value from fields
        String name = mInputName.getText().toString();
        String phone = mInputPhone.getText().toString();
        int selectedGender = mRgrupGender.getCheckedRadioButtonId();
        int gender = 0;
        if(selectedGender == mRbtnIsMale.getId()) gender = 1;
        else if(selectedGender == mRbtnIsFemale.getId()) gender = 2;

        int is_tutor = 0;
        int is_student = 0;
        int selectedUserType = mRgrupUserOpt.getCheckedRadioButtonId();

        if(selectedUserType == mRbtnIsTutor.getId()) is_tutor = 1;
        else if(selectedUserType == mRbtnIsStudent.getId()) is_student = 1;
        else if(selectedUserType == mRbtnIsBoth.getId()){
            is_tutor = 1;
            is_student = 1;
        }

        MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id_user", mUserLogin.getId()+"")
                .addFormDataPart("name", name)
                .addFormDataPart("phone", phone)
                .addFormDataPart("gender", gender+"")
                .addFormDataPart("is_tutor", is_tutor+"")
                .addFormDataPart("is_student", is_student+"")
                .addFormDataPart("user_photo", mUserLogin.getId()+".jpg", RequestBody.create(MEDIA_TYPE_JPG, new File(mSavedPhotoUri.getPath())) )
                .build();

        OkHttpClient httpClient = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_SAVE_PROFILE)
                .post(formBody)
                .build();
        final Call httpCall = httpClient.newCall(httpRequest);

        mProgressDialog.setMessage("Mengirimkan data...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                httpCall.cancel();
                App.showSimpleDialog(CompleteProfileActivity.this, "Proses kirim data dibatalkan");
                mVerticalStepperForm.setActiveStepAsCompleted();
            }
        });
        if(!mProgressDialog.isShowing()) mProgressDialog.show();
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new RunnableDialogMessage(mContext, dialogTitle, String.valueOf(e), mProgressDialog));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mVerticalStepperForm.setActiveStepAsCompleted();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
                    }
                });
                String json = response.body().string();
                if(response.isSuccessful() && response.code() == 200){
                    try {
                        JSONObject objResponse = new JSONObject(json);
                        int status = objResponse.getInt("success");
                        if(status == 1){
                            // retrieve user data from http response
                            String userData = objResponse.getString("data");
                            JSONObject objUserData = new JSONObject(userData);
                            // set another attribute's value
                            mUserLogin.setName(objUserData.getString(mSession.KEY_NAME));
                            mUserLogin.setGender(objUserData.getInt(mSession.KEY_GENDER));
                            mUserLogin.setPhone(objUserData.getString(mSession.KEY_PHONE));
                            mUserLogin.setPhotoUrl(objUserData.getString(mSession.KEY_PHOTO_URL));
                            mUserLogin.setIsTutor(objUserData.getInt(mSession.KEY_IS_TUTOR));
                            mUserLogin.setIsStudent(objUserData.getInt(mSession.KEY_IS_STUDENT));
                            mUserLogin.setIsProfileComplete(objUserData.getInt(mSession.KEY_IS_PROFILE_COMPLETE));
                            mUserLogin.setIsAvailable(objUserData.getInt(mSession.KEY_IS_AVAILABLE));
                            mSession.updateSession(mUserLogin);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(CompleteProfileActivity.this, App.HOME_ACTIVITY);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }else{
                            String message = objResponse.getString("error_msg");
                            runOnUiThread(new RunnableDialogMessage(mContext, dialogTitle, message, mProgressDialog));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mVerticalStepperForm.setActiveStepAsCompleted();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        // alert user
                        runOnUiThread(new RunnableDialogMessage(mContext, dialogTitle, e.getMessage(), mProgressDialog));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mVerticalStepperForm.setActiveStepAsCompleted();
                            }
                        });
                    }
                }else{
                    // alert user
                    runOnUiThread(new RunnableDialogMessage(mContext, dialogTitle, response.message(), mProgressDialog));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mVerticalStepperForm.setActiveStepAsCompleted();
                        }
                    });
                }
            }
        });
    }
}
