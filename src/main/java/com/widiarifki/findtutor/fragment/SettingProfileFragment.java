package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.EducationListAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.helper.DialogMessage;
import com.widiarifki.findtutor.helper.SessionManager;
import com.widiarifki.findtutor.model.Education;
import com.widiarifki.findtutor.model.SubjectTopic;
import com.widiarifki.findtutor.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by widiarifki on 28/05/2017.
 */

public class SettingProfileFragment extends Fragment {

    private Context mContext;
    private Activity mContextActivity;
    private String dialogTitle = "Submit Data Gagal";

    SessionManager mSession;
    User mUserLogin;

    // UI comp.
    LinearLayout mFormLayout;
    EditText mInputName;
    EditText mInputEmail;
    EditText mInputPhone;
    EditText mInputBio;
    ImageView mImgUserPhoto;
    ListView mListviewEducation;
    Button mBtnSave;
    Button mBtnAddEdu;
    Button mBtnChooseSubject;

    ProgressDialog mProgressDialog;

    int mSchoolLevelListLn;
    String[] mSchoolLevelList;
    HashMap<Integer,String> mSchoolLevelMap;

    List<Education> mEducations;
    EducationListAdapter mEduListAdapter;
    ArrayList<SubjectTopic> mSubjects;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveChanges();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        View view = inflater.inflate(R.layout.fragment_setting_profile, container, false);

        mSession = new SessionManager(mContext);
        mUserLogin = mSession.getUserDetail();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);
        settleRefData();

        // Bind UI comp.
        mFormLayout = (LinearLayout) view.findViewById(R.id.form_layout);
        mInputName = (EditText) view.findViewById(R.id.input_name);
        mInputEmail = (EditText) view.findViewById(R.id.input_email);
        mInputPhone = (EditText) view.findViewById(R.id.input_phone);
        mInputBio = (EditText) view.findViewById(R.id.input_bio);

        mEducations = new ArrayList<Education>();
        mEduListAdapter = new EducationListAdapter(mContext, mEducations);
        mListviewEducation = (ListView) view.findViewById(R.id.list_education);
        mListviewEducation.setAdapter(mEduListAdapter);
        mListviewEducation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                final String[] actions = {"Edit", "Hapus"};
                dialogBuilder.setItems(actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedAction = actions[which];
                        if(selectedAction == "Edit"){
                            //mFormLayout.requestFocus();
                            actionUpdateEducation(false, position);
                        }
                        else if(selectedAction == "Hapus"){
                            dialog.dismiss();
                            AlertDialog.Builder dialogConfBuilder = new AlertDialog.Builder(mContext);
                            dialogConfBuilder.setTitle(getString(R.string.delete_confirmation));
                            dialogConfBuilder.setMessage("Apakah anda yakin akan menghapus data ini?");
                            dialogConfBuilder.setCancelable(true);
                            dialogConfBuilder.setNegativeButton(mContext.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            dialogConfBuilder.setPositiveButton(mContext.getString(R.string.cast_tracks_chooser_dialog_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mEducations.remove(position);
                                    mEduListAdapter.notifyDataSetChanged();
                                    App.setListViewHeightBasedOnChildren(mListviewEducation);

                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertDialogConf = dialogConfBuilder.create();
                            alertDialogConf.show();
                        }
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });

        /*mBtnSave = (Button) view.findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });*/
        mBtnAddEdu = (Button) view.findViewById(R.id.btn_add_edu);
        mBtnAddEdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mFormLayout.requestFocus();
                actionUpdateEducation(true, 0);
            }
        });

        /*mBtnChooseSubject = (Button) view.findViewById(R.id.btn_choose_subject);
        mBtnChooseSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SelectSubjectFragment();
                ((MainActivity)mContext).addStackedFragment(fragment, getString(R.string.action_select_subject), getString(R.string.title_activity_edit_profile));
            }
        });
        mSavedSubject = ((MainActivity)getActivity()).getSelectedSubject();*/

        bindInitialData();

        return view;
    }

    void bindInitialData(){
        mUserLogin = mSession.getUserDetail();
        mInputName.setText(mUserLogin.getName());
        mInputEmail.setText(mUserLogin.getEmail());
        mInputPhone.setText(mUserLogin.getPhone());
        mInputBio.setText(mUserLogin.getBio());
        mEducations.clear();
        mEduListAdapter.notifyDataSetChanged();
        if(mUserLogin.getEducations()!=null) {
            if (mUserLogin.getEducations().size() > 0) {
                mEducations.addAll(mUserLogin.getEducations());
                mEduListAdapter.notifyDataSetChanged();
                App.setListViewHeightBasedOnChildren(mListviewEducation);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void settleRefData() {
        mProgressDialog.setMessage("Tunggu sebentar...");
        if(!mProgressDialog.isShowing()) mProgressDialog.show();

        OkHttpClient client = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_GET_SCHOOL_LEVEL)
                .build();
        Call httpCall = client.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                if(response.isSuccessful() && response.code() == 200){
                    try {
                        JSONArray dataJson = new JSONArray(json);
                        mSchoolLevelListLn = dataJson.length();
                        mSchoolLevelList = new String[mSchoolLevelListLn];
                        mSchoolLevelMap = new HashMap<Integer, String>();
                        for (int i = 0; i < mSchoolLevelListLn; i++) {
                            JSONObject dataObj = dataJson.getJSONObject(i);
                            mSchoolLevelList[i] = dataObj.getString("REF_DESC");
                            mSchoolLevelMap.put(i, dataObj.getString("REF_CODE"));
                        }
                        /** Hide progress bae **/
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mProgressDialog.isShowing()) mProgressDialog.hide();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{

                }
            }
        });
    }

    private void actionUpdateEducation(final boolean isNewData, final int editedItemPos){
        View currentFocus = getActivity().getCurrentFocus();
        if (currentFocus != null) currentFocus.clearFocus();
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);*/

        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(currentFocus.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

        //mBtnAddEdu.requestFocus();

        final View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_add_edu, null);
        final EditText inputYear = (EditText) dialogView.findViewById(R.id.input_year);
        final EditText inputSchoolName = (EditText) dialogView.findViewById(R.id.input_school_name);
        final EditText inputSchoolDept = (EditText) dialogView.findViewById(R.id.input_department);
        final Spinner ddSchoolLevel = (Spinner) dialogView.findViewById(R.id.spinner_school_level);
        ArrayAdapter<String> listSchoolLevel = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mSchoolLevelList);
        listSchoolLevel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddSchoolLevel.setAdapter(listSchoolLevel);

        if(!isNewData){
            // Bind data to form
            Education editedData = mEducations.get(editedItemPos);
            inputYear.setText(editedData.getYearGraduate());
            inputSchoolName.setText(editedData.getSchoolName());
            inputSchoolDept.setText(editedData.getDepartment());
            ddSchoolLevel.setSelection(listSchoolLevel.getPosition(editedData.getSchoolLevelText()));
        }

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setTitle(mContext.getString(R.string.action_add_education));
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setNegativeButton(mContext.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.setPositiveButton(mContext.getString(R.string.cast_tracks_chooser_dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Education education = new Education();
                education.setIdUser(mUserLogin.getId());
                education.setYearGraduate(inputYear.getText().toString());
                int selectedIndex = ddSchoolLevel.getSelectedItemPosition();
                education.setSchoolLevel(Integer.parseInt(mSchoolLevelMap.get(selectedIndex)));
                education.setSchoolLevelText(ddSchoolLevel.getSelectedItem().toString());
                education.setSchoolName(inputSchoolName.getText().toString());
                education.setDepartment(inputSchoolDept.getText().toString());

                if(isNewData) {
                    mEduListAdapter.addToList(education);
                    App.setListViewHeightBasedOnChildren(mListviewEducation);
                }else{
                    mEducations.set(editedItemPos, education);
                    mEduListAdapter.notifyDataSetChanged();
                }

                //View currentFocus = getActivity().getCurrentFocus();
                //if (currentFocus != null) currentFocus.clearFocus();
                //dialogView.requestFocus();
                mFormLayout.requestFocus();
                View currentFocus = getActivity().getCurrentFocus();
                if (currentFocus != null) currentFocus.clearFocus();
                //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);*/

                InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(currentFocus.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                //dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        if(!alertDialog.isShowing()) alertDialog.show();
    }

    void saveChanges(){
        // Store values
        String name = mInputName.getText().toString();
        String email = mInputEmail.getText().toString();
        String phone = mInputPhone.getText().toString();
        String bio = mInputBio.getText().toString();

        // Pass validation
        mProgressDialog.setMessage("Menyimpan perubahan...");
        if(!mProgressDialog.isShowing()) mProgressDialog.show();

        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        // convert your list to json
        String jsonEducationList = new Gson().toJson(mEducations);

        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id_user", mUserLogin.getId()+"")
                .addFormDataPart("email", email)
                .addFormDataPart("name", name)
                .addFormDataPart("phone", phone)
                .addFormDataPart("bio", bio)
                .addFormDataPart("educations", null, RequestBody.create(MEDIA_TYPE_JSON, jsonEducationList))
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
                getActivity().runOnUiThread(new DialogMessage(mContext, dialogTitle, String.valueOf(e), mProgressDialog));
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
                            mUserLogin.setName(objUserData.getString(mSession.KEY_NAME));
                            mUserLogin.setEmail(objUserData.getString(mSession.KEY_EMAIL));
                            mUserLogin.setPhone(objUserData.getString(mSession.KEY_PHONE));
                            mUserLogin.setBio(objUserData.getString(mSession.KEY_BIO));
                            Type type = new TypeToken<List<Education>>(){}.getType();
                            List<Education> educationList = new Gson().fromJson(objUserData.getString(mSession.KEY_EDUCATIONS), type);
                            mUserLogin.setEducations(educationList);
                            mSession.updateSession(mUserLogin);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(mProgressDialog.isShowing()) mProgressDialog.hide();
                                    Toast.makeText(mContext, "Simpan profil berhasil", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            String message = responseObj.getString("error_msg");
                            // alert user
                            getActivity().runOnUiThread(new DialogMessage(mContext, dialogTitle, message, mProgressDialog));
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