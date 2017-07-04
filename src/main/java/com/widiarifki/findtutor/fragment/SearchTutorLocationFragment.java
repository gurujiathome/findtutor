package com.widiarifki.findtutor.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.GooglePlacesAutocompleteAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.model.GooglePlace;
import com.widiarifki.findtutor.service.FetchAddressIntentService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by widiarifki on 26/06/2017.
 */

public class SearchTutorLocationFragment extends Fragment
        implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int ACTION_PRINT_ADDRESS_TO_TEXT = 1;
    Context mContext;
    AppCompatActivity mContextActivity;
    private MapView mMapView;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderApi mFusedLocationClient;
    TextView mTvLocation;

    Location mSelectedLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.appbar_menu, menu);
        //menu.findItem(R.id.action_next).setVisible(true);
        menu.findItem(R.id.action_save).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_next) {
            /*((MainActivity)mContext).addStackedFragment(new SearchTutorDateFragment(), getString(R.string.title_search_select_date), getString(R.string.title_search_select_location));
            MainActivity.mSearchTutorLocation = mSelectedLocation;*/
        }
        else if(id == R.id.action_save){
            App.hideSoftKeyboard(mContext);
            ((MainActivity)mContext).removeFragmentFromStack(this);
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        mContextActivity = (AppCompatActivity)mContext;
        buildGoogleApi();

        View view = inflater.inflate(R.layout.fragment_search_tutor_location, container, false);

        final ImageButton btnClear = (ImageButton) view.findViewById(R.id.btn_clear_autocomplete);
        final AutoCompleteTextView autoCompView = (AutoCompleteTextView) view.findViewById(R.id.input_search);
        final KeyListener autoCompViewListener = autoCompView.getKeyListener();
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(mContext, R.layout.item_layout_autocomplete_place));
        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GooglePlace selectedPlace = (GooglePlace) parent.getItemAtPosition(position);
                final String fullPlaceDesc = selectedPlace.getName() + ", " + selectedPlace.getAddress().trim();
                autoCompView.setText(fullPlaceDesc);
                btnClear.setVisibility(View.VISIBLE);
                autoCompView.setKeyListener(null);
                App.hideSoftKeyboard(mContext);
                /*InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(((AppCompatActivity) mContext).getCurrentFocus() != null)
                    inputManager.hideSoftInputFromWindow(((AppCompatActivity) mContext).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);*/

                String placeId = (String) view.getTag();
                String apiKey = mContext.getString(R.string.api_key);
                String baseUrl = "https://maps.googleapis.com/maps/api/place/details/";
                String format = "json";

                OkHttpClient client = new OkHttpClient();
                Request httpRequest = new Request.Builder()
                        .url(baseUrl + format + "?" +
                                "key=" + apiKey +
                                "&placeid=" + placeId +
                                "&language=id")
                        .build();
                Call httpCall = client.newCall(httpRequest);
                httpCall.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        mContextActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if(response.isSuccessful() && response.code() == 200){
                            String json = response.body().string();
                            try {
                                JSONObject jsonObj = new JSONObject(json);
                                JSONObject dataObj = jsonObj.getJSONObject("result");
                                JSONObject dataGeo = dataObj.getJSONObject("geometry");
                                JSONObject dataLoc = dataGeo.getJSONObject("location");
                                mSelectedLocation = new Location("");
                                mSelectedLocation.setLatitude(dataLoc.getDouble("lat"));
                                mSelectedLocation.setLongitude(dataLoc.getDouble("lng"));
                                mContextActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateMapMarker(fullPlaceDesc);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            mContextActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT);
                                }
                            });
                        }
                    }
                });
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompView.setText("");
                btnClear.setVisibility(View.GONE);
                autoCompView.setKeyListener(autoCompViewListener);
            }
        });

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
                if (s.length() == 0) {
                    mTvLocation.setVisibility(View.GONE);
                } else {
                    mTvLocation.setVisibility(View.VISIBLE);
                }
            }
        });

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                /*checkLocationPermission();
                googleMap.setMyLocationEnabled(true);*/

                fetchLastLocation();
            }
        });

        return view;
    }

    private void fetchLastLocation() {
        if(mFusedLocationClient != null){
            checkLocationPermission();
            mSelectedLocation = mFusedLocationClient.getLastLocation(mGoogleApiClient);
            if(mSelectedLocation != null) {
                updateMapMarker();
            }else{
                fetchCurrentLocation();
            }
        }
    }

    private void fetchCurrentLocation() {
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
                        mSelectedLocation = locationList.get(0);
                        updateMapMarker();
                        stopLocationUpdates();
                    }
                }else{
                    Toast.makeText(mContext, "Tidak bisa mendeteksi lokasi terkini anda", Toast.LENGTH_LONG).show();
                }
            }
        }

        ;
    };

    private void stopLocationUpdates() {
        if(mFusedLocationClient != null)
            mFusedLocationClient.removeLocationUpdates(mGoogleApiClient, mLocationCallback);
        //mGoogleApiClient.disconnect();
    }

    private void updateMapMarker(String locationText) {
        if(mSelectedLocation != null){
            googleMap.clear();
            // For dropping a marker at a point on the Map
            LatLng sydney = new LatLng(mSelectedLocation.getLatitude(), mSelectedLocation.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

            // For zooming automatically to the location of the marker
            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            mTvLocation.setText(locationText);
            // Transfer val to activity
            ((MainActivity)mContext).mSearchTutorLocation = mSelectedLocation;
            ((MainActivity)mContext).mSearchTutorLocationTxt = locationText;
        }
    }

    private void updateMapMarker() {
        if(mSelectedLocation != null){
            googleMap.clear();
            // For dropping a marker at a point on the Map
            LatLng sydney = new LatLng(mSelectedLocation.getLatitude(), mSelectedLocation.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

            // For zooming automatically to the location of the marker
            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            fetchAddressLocation(mSelectedLocation, ACTION_PRINT_ADDRESS_TO_TEXT);
        }
    }

    protected void fetchAddressLocation(final Location location, final int actionCode) {
        if (location == null) {
            mTvLocation.setText("Lokasi tidak terdeteksi");
        } else {
            Intent intent = new Intent(mContext, FetchAddressIntentService.class);
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
            intent.putExtra(Constants.RECEIVER, new ResultReceiver(null) {
                @Override
                protected void onReceiveResult(final int resultCode, final Bundle resultData) {
                    mContextActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultCode == Constants.SUCCESS_RESULT) {
                                final String address = resultData.getString(Constants.RESULT_DATA_KEY);
                                if(actionCode == ACTION_PRINT_ADDRESS_TO_TEXT){
                                    mTvLocation.setText(address);
                                    // Transfer val to activity
                                    ((MainActivity)mContext).mSearchTutorLocation = mSelectedLocation;
                                    ((MainActivity)mContext).mSearchTutorLocationTxt = address;
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

    private void checkLocationPermission() {
        // For showing a move to my location button
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

    private void buildGoogleApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                //.enableAutoManage(mContext, 0, null)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
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
    public void onConnected(@Nullable Bundle bundle) {
        mFusedLocationClient = LocationServices.FusedLocationApi;
        if(mSelectedLocation == null) {
            fetchLastLocation();
        }
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