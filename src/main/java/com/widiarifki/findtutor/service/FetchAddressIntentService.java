package com.widiarifki.findtutor.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;

import com.widiarifki.findtutor.app.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by widiarifki on 21/06/2017.
 */

public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "FindTutor";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public FetchAddressIntentService() {
        super(FetchAddressIntentService.class.getSimpleName());
    }

    ResultReceiver resultReceiver;
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        String errorMessage = "";

        String baseUrl = "https://maps.googleapis.com/maps/api/geocode/";
        String format = "json";

        OkHttpClient client = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(baseUrl + format + "?" +
                        "language=id&" +
                        "latlng="+location.getLatitude()+","+location.getLongitude())
                .build();
        Call httpCall = client.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverResultToReceiver(Constants.FAILURE_RESULT, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                if(response.isSuccessful() && response.code() == 200){
                    try {
                        JSONObject jsonObj = new JSONObject(json);
                        String status = jsonObj.getString("status");
                        if(status.equalsIgnoreCase("OK")){
                            JSONArray results = jsonObj.getJSONArray("results");
                            if(results.length() > 0) {
                                JSONObject firstResult = results.getJSONObject(0);
                                String address = firstResult.getString("formatted_address");
                                deliverResultToReceiver(Constants.SUCCESS_RESULT, address);
                            }
                        }else{
                            deliverResultToReceiver(Constants.FAILURE_RESULT, jsonObj.getString("error_message"));
                        }
                    } catch (JSONException e) {
                        deliverResultToReceiver(Constants.FAILURE_RESULT, e.getMessage());
                    }
                }else{
                    deliverResultToReceiver(Constants.FAILURE_RESULT, response.message());
                }
            }
        });
        /*
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, ioException.getMessage());
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            //Log.e(TAG, errorMessage + ". " +
                    //"Latitude = " + location.getLatitude() +
                    //", Longitude = " + location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                //Log.e(TAG, errorMessage);
            }

            // Gunakan cara ke-2 untuk melakukan geocoding
            String baseUrl = "https://maps.googleapis.com/maps/api/geocode/";
            String format = "json";

            OkHttpClient client = new OkHttpClient();
            Request httpRequest = new Request.Builder()
                    .url(baseUrl + format + "?" +
                            "language=id&" +
                            "latlng="+location.getLatitude()+","+location.getLongitude())
                    .build();
            Call httpCall = client.newCall(httpRequest);
            httpCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    deliverResultToReceiver(Constants.FAILURE_RESULT, e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    if(response.isSuccessful() && response.code() == 200){
                        try {
                            JSONObject jsonObj = new JSONObject(json);
                            String status = jsonObj.getString("status");
                            if(status.equalsIgnoreCase("OK")){
                                JSONArray results = jsonObj.getJSONArray("results");
                                if(results.length() > 0) {
                                    JSONObject firstResult = results.getJSONObject(0);
                                    String address = firstResult.getString("formatted_address");
                                    deliverResultToReceiver(Constants.SUCCESS_RESULT, address);
                                }
                            }else{
                                String errorMsg = jsonObj.getString("error_message");
                                deliverResultToReceiver(Constants.FAILURE_RESULT, errorMsg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        deliverResultToReceiver(Constants.FAILURE_RESULT, response.message());
                    }
                }
            });
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            //Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(
                    Constants.SUCCESS_RESULT,
                    //TextUtils.join(System.getProperty("line.separator"), addressFragments)
                    TextUtils.join(" ", addressFragments)
            );
        }*/
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        resultReceiver.send(resultCode, bundle);
    }
}
