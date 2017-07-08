package com.widiarifki.findtutor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.model.GooglePlace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by widiarifki on 29/06/2017.
 */

public class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

    private static Context mContext;
    private ArrayList<GooglePlace> resultList;

    public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_place_autocomplete, null);
        }

        GooglePlace place = getItem(position);
        String name = place.getName();
        String address = place.getAddress();

        convertView.setTag(place.getPlaceId());
        TextView tvPlaceName = (TextView) convertView.findViewById(R.id.tvPlaceName);
        tvPlaceName.setText(name);
        TextView tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
        tvAddress.setText(address);

        return convertView;
    }

    @Override
    public int getCount() {
        return resultList != null ? resultList.size() : 0;
    }

    @Override
    public GooglePlace getItem(int index) {
        return (GooglePlace) resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private static final String LOG_TAG = "Google Places";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    public static ArrayList autocomplete(String input) {
        String apiKey = mContext.getString(R.string.api_key);
        /*final ArrayList[] resultList = {null};

        String encodedInput = "";
        try {
            encodedInput = URLEncoder.encode(input, "utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON + "?" +
                        "key=" + apiKey +
                        "&components=country:id" +
                        "&language=id" +
                        "&input=" + encodedInput)
                .build();
        Call httpCall = client.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code() == 200 && response.isSuccessful()){
                    String json = response.body().string();
                    // Create a JSON object hierarchy from the results
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(json);
                        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

                        // Extract the Place descriptions from the results
                        resultList[0] = new ArrayList(predsJsonArray.length());
                        for (int i = 0; i < predsJsonArray.length(); i++) {
                            //System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                            //System.out.println("============================================================");
                            //resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                            JSONObject prediction = predsJsonArray.getJSONObject(i);
                            JSONObject structuredFormatting = prediction.getJSONObject("structured_formatting");
                            GooglePlace newPlace = new GooglePlace();
                            newPlace.setName(structuredFormatting.getString("main_text"));
                            newPlace.setAddress(structuredFormatting.getString("secondary_text"));
                            newPlace.setPlaceId(prediction.getString("place_id"));
                            resultList[0].add(newPlace);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT);
                }
            }
        });*/

        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + apiKey);
            sb.append("&components=country:id");
            sb.append("&language=id");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            //Log.e(LOG_TAG, "Error processing", e);
            return resultList;
        } catch (IOException e) {
            //Log.e(LOG_TAG, "Error connecting", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                //System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                //System.out.println("============================================================");
                //resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                JSONObject prediction = predsJsonArray.getJSONObject(i);
                JSONObject structuredFormatting = prediction.getJSONObject("structured_formatting");
                GooglePlace newPlace = new GooglePlace();
                newPlace.setName(structuredFormatting.getString("main_text"));
                newPlace.setAddress(structuredFormatting.getString("secondary_text"));
                newPlace.setPlaceId(prediction.getString("place_id"));
                resultList.add(newPlace);
            }
        } catch (JSONException e) {
            //Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        //return resultList[0];
        return resultList;
    }
}