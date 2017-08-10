package com.widiarifki.findtutor.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.SubjectListAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.helper.RunnableDialogMessage;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.SubjectTopic;
import com.widiarifki.findtutor.model.User;
import com.widiarifki.findtutor.service.FetchAddressIntentService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by widiarifki on 05/06/2017.
 */

public class SettingTutorPrefFragment extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Context mContext;
    private Activity mContextActivity;
    MainActivity mParentActivity;
    private String dialogTitle = "Submit Data Gagal";

    SessionManager mSession;
    User mUserLogin;

    // UI comp.
    LinearLayout mFormLayout;
    TextView mTvLocation;
    Button mBtnSetCurrentLocation;
    Button mBtnSetSearchLocation;
    SeekBar mSeekBarMaxDistance;
    SeekBar mSeekBarMinPrice;
    ListView mListViewSubject;
    Button mBtnChooseSubject;

    ProgressDialog mProgressDialog;

    HashMap<String, SavedSubject> mSavedSubject;
    SubjectListAdapter mSubjectListAdapter;
    ArrayList<String> mSavedSubjectTopic = new ArrayList<String>();
    Location mLocation;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderApi mFusedLocationClient;
    int REQUEST_PLACE_PICKER = 1;
    int ACTION_PRINT_ADDRESS_TO_TEXT = 1;
    int ACTION_SHOW_ADDRESS_TO_DIALOG = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.appbar_menu, menu);
        menu.findItem(R.id.action_save).setVisible(true);
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
        mContextActivity = (Activity) mContext;
        mParentActivity = (MainActivity) mContext;

        buildGoogleApi();

        final View view = inflater.inflate(R.layout.fragment_setting_tutor_pref, container, false);

        mSession = new SessionManager(mContext);
        mUserLogin = mSession.getUserDetail();

        mProgressDialog = new ProgressDialog(mContext);

        // Bind UI comp.
        mFormLayout = (LinearLayout) view.findViewById(R.id.form_layout);
        mTvLocation = (TextView) view.findViewById(R.id.text_location);
        mTvLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) mTvLocation.setVisibility(View.GONE);
                else mTvLocation.setVisibility(View.VISIBLE);
            }
        });
        mBtnSetCurrentLocation = (Button) view.findViewById(R.id.btn_set_current_location);
        mBtnSetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCurrentLocation();
            }
        });
        mBtnSetSearchLocation = (Button) view.findViewById(R.id.btn_set_searched_location);
        mBtnSetSearchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickLocation();
            }
        });
        mSeekBarMaxDistance = (SeekBar) view.findViewById(R.id.seekbar_max_distance);
        mSeekBarMaxDistance.setMax(App.SEEKBAR_TRAVEL_DISTANCE_MAX);
        mSeekBarMaxDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < App.SEEKBAR_TRAVEL_DISTANCE_MIN)
                    progress = App.SEEKBAR_TRAVEL_DISTANCE_MIN;
                TextView preview = (TextView) view.findViewById(R.id.preview_max_distance);
                preview.setText("(" + progress + " KM)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekBarMinPrice = (SeekBar) view.findViewById(R.id.seekbar_min_price_rate);
        mSeekBarMinPrice.setMax(App.SEEKBAR_PRICE_RATE_MAX);
        mSeekBarMinPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / App.SEEKBAR_PRICE_RATE_RANGE;
                progress = progress * App.SEEKBAR_PRICE_RATE_RANGE;
                TextView preview = (TextView) view.findViewById(R.id.preview_min_price_rate);
                preview.setText("(Rp. " + progress + " / Jam)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mListViewSubject = (ListView) view.findViewById(R.id.list_subject);
        mBtnChooseSubject = (Button) view.findViewById(R.id.btn_choose_subject);
        mBtnChooseSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SettingSelectSubjectFragment();
                mParentActivity.addStackedFragment(fragment, getString(R.string.action_select_subject), getString(R.string.set_tutor_preference));
            }
        });

        /*mSavedSubject = ((MainActivity)getActivity()).getSelectedSubject();
        mSubjectListAdapter = new SubjectListAdapter(mContext, mSavedSubject);
        mListViewSubject = (ListView) view.findViewById(R.id.list_subject);
        mListViewSubject.setAdapter(mSubjectListAdapter);
        mSubjectListAdapter.notifyDataSetChanged();
        App.setListViewHeightBasedOnChildren(mListViewSubject);*/

        bindInitialData();
        //fetchDetectedLocationAddress();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void bindInitialData() {
        // Save saved location in activity variable
        if(mParentActivity.mTempSavedLocation == null){
            // this case usually when fragment load for 1st time
            String userLatitude = mUserLogin.getLatitude();
            String userLongitude = mUserLogin.getLongitude();
            if ((userLatitude != null && !userLatitude.isEmpty()) && (userLongitude != null && !userLongitude.isEmpty())) {
                // instantiate location
                /*mLocation = new Location("");
                mLocation.setLatitude(Double.parseDouble(mUserLogin.getLatitude()));
                mLocation.setLongitude(Double.parseDouble(mUserLogin.getLongitude()));*/
                Location location = new Location("");
                location.setLatitude(Double.parseDouble(mUserLogin.getLatitude()));
                location.setLongitude(Double.parseDouble(mUserLogin.getLongitude()));
                if(mUserLogin.getLocationAddress() != null && !(mUserLogin.getLocationAddress()).isEmpty()){
                    updateSelectedLocation(location, mUserLogin.getLocationAddress());
                }else{
                    // fetch the address
                    mProgressDialog.setMessage("Memuat lokasi...");
                    if (!mProgressDialog.isShowing()) mProgressDialog.show();
                    //fetchDetectedLocationAddress(mLocation, ACTION_PRINT_ADDRESS_TO_TEXT);
                    fetchDetectedLocationAddress(location, ACTION_PRINT_ADDRESS_TO_TEXT);
                }
            }
        }else{
            // this case usually when fragment is RE-loaded
            //mLocation = mParentActivity.mTempSavedLocation;
            if(mParentActivity.mTempSavedLocationAddress != null){
                mTvLocation.setText(mParentActivity.mTempSavedLocationAddress);
            }
        }

        /*String userLatitude = mUserLogin.getLatitude();
        String userLongitude = mUserLogin.getLongitude();
        if ((userLatitude != null && !userLatitude.isEmpty()) && (userLongitude != null && !userLongitude.isEmpty())) {
            mLocation = new Location("");
            mLocation.setLatitude(Double.parseDouble(mUserLogin.getLatitude()));
            mLocation.setLongitude(Double.parseDouble(mUserLogin.getLongitude()));
            mProgressDialog.setMessage("Memuat lokasi...");
            if (!mProgressDialog.isShowing()) mProgressDialog.show();
            fetchDetectedLocationAddress(mLocation, ACTION_PRINT_ADDRESS_TO_TEXT);
        }*/
        if (mUserLogin.getMaxTravelDistance() == 0)
            mSeekBarMaxDistance.setProgress(App.SETTING_TRAVEL_DISTANCE_MAX_DEFAULT);
        else
            mSeekBarMaxDistance.setProgress(mUserLogin.getMaxTravelDistance());

        mSeekBarMinPrice.setProgress(mUserLogin.getMinPriceRate());
        /*if(mUserLogin.getSubjects()==null){
            mSavedSubject = ((MainActivity)getActivity()).getSelectedSubject();
        }else{
            mSavedSubject = mUserLogin.getSubjects();
        }*/
        mSavedSubject = mParentActivity.getSelectedSubject();
        mSubjectListAdapter = new SubjectListAdapter(mContext, mSavedSubject);
        mListViewSubject.setAdapter(mSubjectListAdapter);
        mSubjectListAdapter.notifyDataSetChanged();
        App.setListViewHeightBasedOnChildren(mListViewSubject);
    }

    private void fetchCurrentLocation() {
        // Show mDialog
        mProgressDialog.setMessage("Mendapatkan lokasi...");
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stopLocationUpdates();
            }
        });
        if (!mProgressDialog.isShowing()) mProgressDialog.show();

        // Ensure mGoogleApiClient
        if (mGoogleApiClient == null) {
            buildGoogleApi();
        } else {
            if (!mGoogleApiClient.isConnected())
                mGoogleApiClient.connect();
        }

        // Request Location
        if (mGoogleApiClient.isConnected()) {
            checkLocationPermission();
            mFusedLocationClient.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationCallback, null);
        }
    }

    private void pickLocation() {
        // Ensure mGoogleApiClient
        if (mGoogleApiClient == null) {
            buildGoogleApi();
        } else {
            if (!mGoogleApiClient.isConnected())
                mGoogleApiClient.connect();
        }

        if (mGoogleApiClient.isConnected()) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(mContextActivity), REQUEST_PLACE_PICKER);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(mContext, data);
                LatLng placeLatLng = place.getLatLng();
                String address = place.getName() + "\n" + place.getAddress();
                Location location = new Location("");
                location.setLongitude(placeLatLng.longitude);
                location.setLatitude(placeLatLng.latitude);
                updateSelectedLocation(location, address);
                //fetchDetectedLocationAddress(mLocation, ACTION_PRINT_ADDRESS_TO_TEXT);
            }
        }
    }

    void buildGoogleApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                //.enableAutoManage(mContext, 0, null)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private LocationRequest mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(1000); // Update location every second

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                final List<Location> locationList = locationResult.getLocations();
                if (locationList != null) {
                    if (locationList.size() > 0) {
                        fetchDetectedLocationAddress(locationList.get(0), ACTION_SHOW_ADDRESS_TO_DIALOG);
                        stopLocationUpdates();
                    }
                }
            }else{
                if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
                App.showSimpleDialog(mContext, "Lokasi anda saat ini tidak terdeteksi. Coba dengan cara mencari lokasi");
            }
        }

        ;
    };

    protected void fetchDetectedLocationAddress(final Location location, final int actionCode) {
        if (location == null) {
            mTvLocation.setText("Lokasi tidak terdeteksi");
        } else {
            Intent intent = new Intent(mContext, FetchAddressIntentService.class);
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
            intent.putExtra(Constants.RECEIVER, new ResultReceiver(null) {
                @Override
                protected void onReceiveResult(final int resultCode, final Bundle resultData) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultCode == Constants.SUCCESS_RESULT) {
                                if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
                                final String address = resultData.getString(Constants.RESULT_DATA_KEY);
                                if(actionCode == ACTION_PRINT_ADDRESS_TO_TEXT){
                                    updateSelectedLocation(location, address);
                                }
                                else if(actionCode == ACTION_SHOW_ADDRESS_TO_DIALOG){
                                    new AlertDialog.Builder(mContext)
                                        .setTitle("Gunakan lokasi ini")
                                        .setMessage(address)
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                updateSelectedLocation(location, address);
                                                dialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .create()
                                        .show();
                                }
                            } else {
                                mTvLocation.setText("Lokasi anda tidak terdeteksi");
                            }
                        }
                    });
                }
            });
            mContext.startService(intent);
        }
    }

    void updateSelectedLocation(Location location, String address){
        //mParentActivity.mTempSavedLocation = mLocation = location;
        mParentActivity.mTempSavedLocation = location;
        mParentActivity.mTempSavedLocationAddress = address;
        mTvLocation.setText(address);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }

    private void saveChanges() {
        // Store values
        String latitude = "";
        String longitude = "";
        String locationAddress = "";
        /*if(mLocation != null){
            latitude = mLocation.getLatitude()+"";
            longitude = mLocation.getLongitude()+"";
        }*/
        if(mParentActivity.mTempSavedLocation != null){
            Location location = mParentActivity.mTempSavedLocation;
            latitude = location.getLatitude()+"";
            longitude = location.getLongitude()+"";
        }

        if(mParentActivity.mTempSavedLocationAddress != null)
            locationAddress = mParentActivity.mTempSavedLocationAddress;

        int maxDistance = mSeekBarMaxDistance.getProgress();
        if (maxDistance < App.SEEKBAR_TRAVEL_DISTANCE_MIN)
            maxDistance = App.SEEKBAR_TRAVEL_DISTANCE_MIN;
        int minPriceRawVal = mSeekBarMinPrice.getProgress();
        int minPrice = minPriceRawVal / App.SEEKBAR_PRICE_RATE_RANGE;
        minPrice = minPrice * App.SEEKBAR_PRICE_RATE_RANGE;
        if (mSavedSubject != null) {
            mSavedSubjectTopic = new ArrayList<String>();
            for (SavedSubject object : mSavedSubject.values()) {
                int idParent = object.getCategoryId();
                HashMap<String, SubjectTopic> topicList = object.getTopicList();
                if (topicList.get(idParent + "") == null) {
                    /** If user not mCheckedSubjectPos 'Semua' **/
                    for (SubjectTopic topic : topicList.values()) {
                        mSavedSubjectTopic.add(topic.getId() + "");
                    }
                } else {
                    mSavedSubjectTopic.add(idParent + "");
                }
            }
        }

        // Pass validation
        mProgressDialog.setMessage("Menyimpan perubahan...");
        if (!mProgressDialog.isShowing()) mProgressDialog.show();

        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        // convert your list to json
        String jsonSubjectList = new Gson().toJson(mSavedSubjectTopic);

        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(mSession.KEY_ID_USER, mUserLogin.getId() + "")
                .addFormDataPart(mSession.KEY_LATITUDE, latitude)
                .addFormDataPart(mSession.KEY_LONGITUDE, longitude)
                .addFormDataPart(mSession.KEY_LOCATION_ADDRESS, locationAddress)
                .addFormDataPart(mSession.KEY_MIN_PRICE_RATE, minPrice + "")
                .addFormDataPart(mSession.KEY_MAX_TRAVEL_DISTANCE, maxDistance + "")
                .addFormDataPart(mSession.KEY_SUBJECTS, null, RequestBody.create(MEDIA_TYPE_JSON, jsonSubjectList))
                .build();

        OkHttpClient httpClient = new OkHttpClient();

        Request httpRequest = new Request.Builder()
                .url(App.URL_EDIT_TUTOR_PREF)
                .post(formBody)
                .build();

        Call httpCall = httpClient.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // alert user
                mContextActivity.runOnUiThread(new RunnableDialogMessage(mContext, dialogTitle, String.valueOf(e), mProgressDialog));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        JSONObject responseObj = new JSONObject(responseStr);
                        int status = responseObj.getInt("success");

                        if (status == 1) {
                            // Retrieve user data from http response
                            String userData = responseObj.getString("data");
                            JSONObject objUserData = new JSONObject(userData);
                            // Store in Session w/ User object
                            mUserLogin.setLatitude(objUserData.getString(mSession.KEY_LATITUDE));
                            mUserLogin.setLongitude(objUserData.getString(mSession.KEY_LONGITUDE));
                            mUserLogin.setLocationAddress(objUserData.getString(mSession.KEY_LOCATION_ADDRESS));
                            mUserLogin.setMinPriceRate(objUserData.getInt(mSession.KEY_MIN_PRICE_RATE));
                            mUserLogin.setMaxTravelDistance(objUserData.getInt(mSession.KEY_MAX_TRAVEL_DISTANCE));
                            Type type = new TypeToken<Map<String, SavedSubject>>() {}.getType();
                            Map<String, SavedSubject> subjectMap = new Gson().fromJson(objUserData.getString(mSession.KEY_SUBJECTS), type);
                            if (subjectMap != null) {
                                HashMap<String, SavedSubject> subjectHashmap = new HashMap<String, SavedSubject>(subjectMap); // cast process
                                mUserLogin.setSubjects(subjectHashmap);
                            } else {
                                mUserLogin.setSubjects(new HashMap<String, SavedSubject>());
                            }
                            mSession.updateSession(mUserLogin);

                            mContextActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                                    Toast.makeText(mContext, "Pengaturan berhasil disimpan", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            String message = responseObj.getString("error_msg");
                            // alert user
                            mContextActivity.runOnUiThread(new RunnableDialogMessage(mContext, dialogTitle, message, mProgressDialog));
                        }
                    } catch (JSONException e) {
                        // alert user
                        mContextActivity.runOnUiThread(new RunnableDialogMessage(mContext, dialogTitle, e.getMessage(), mProgressDialog));
                    }
                } else {
                    // alert user
                    mContextActivity.runOnUiThread(new RunnableDialogMessage(mContext, dialogTitle, response.message(), mProgressDialog));
                }
            }
        });
    }

    /*private LocationCallback mLocationCallbackPlacePicker = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                List<Location> locationList = locationResult.getLocations();
                if (locationList != null) {
                    if (locationList.size() > 0) {
                        //fetchDetectedLocationAddress(locationList.get(0));
                        mTvLocation.setText(fetchDetectedLocationAddress(locationList.get(0)));
                        mFusedLocationClient.removeLocationUpdates(mGoogleApiClient, mLocationCallbackPlacePicker);
                    }
                }
            }
        };
    };*/

    private void stopLocationUpdates() {
        if(mFusedLocationClient != null)
            mFusedLocationClient.removeLocationUpdates(mGoogleApiClient, mLocationCallback);
        //mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mFusedLocationClient = LocationServices.FusedLocationApi;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}

