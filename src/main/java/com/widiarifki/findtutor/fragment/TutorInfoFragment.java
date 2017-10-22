package com.widiarifki.findtutor.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.TabsPagerAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.helper.RoundedCornersTransform;
import com.widiarifki.findtutor.model.Education;
import com.widiarifki.findtutor.model.SavedSubject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by widiarifki on 01/07/2017.
 */

public class TutorInfoFragment extends Fragment {

    Fragment mThisFragment;
    private Context mContext;
    AppCompatActivity mContextActivity;
    int mIdUser;
    String mName;
    String mPhotoUrl;
    String mLocationAddress;
    String mLatitude;
    String mLongitude;
    double mDistance;

    ImageView mImgUserPhoto;
    TextView mTextName;
    RatingBar mRatingBar;
    TextView mTextPrice;
    TextView mTextLocation;
    TextView mTvFaveBy;
    TextView mTvHasFinishedSess;
    Button mBtnBooking;
    Button mBtnBookingReg;
    private TabsPagerAdapter mPagerAdapter;
    private RatingBar faveTutor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThisFragment = this;
        Bundle params = getArguments();
        mIdUser = params.getInt(Constants.PARAM_KEY_ID_USER, 0);
        mName = params.getString(Constants.PARAM_KEY_NAME, null);
        mPhotoUrl = params.getString(Constants.PARAM_KEY_PHOTO_URL, null);
        mLocationAddress = params.getString(Constants.PARAM_KEY_LOCATION_ADDRESS, null);
        mLatitude = params.getString(Constants.PARAM_KEY_LATITUDE, null);
        mLongitude = params.getString(Constants.PARAM_KEY_LONGITUDE, null);
        mDistance = params.getDouble(Constants.PARAM_KEY_DISTANCE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mContextActivity = (AppCompatActivity) mContext;

        View view = inflater.inflate(R.layout.fragment_tutor_info, container, false);
        mImgUserPhoto = (ImageView) view.findViewById(R.id.imgvUserPhoto);
        mTextName = (TextView) view.findViewById(R.id.tvName);
        mTextPrice = (TextView) view.findViewById(R.id.tvPrice);
        mTextLocation = (TextView) view.findViewById(R.id.tvLocation);
        mTvFaveBy = (TextView) view.findViewById(R.id.tvFaveBy);
        mTvHasFinishedSess = (TextView) view.findViewById(R.id.tvHasFinishedSess);
        mBtnBooking = (Button) view.findViewById(R.id.btnBooking);
        mBtnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putInt(Constants.PARAM_KEY_ID_USER, mIdUser);
                params.putString(Constants.PARAM_KEY_NAME, mName);
                params.putDouble(Constants.PARAM_KEY_DISTANCE, mDistance);
                Fragment fragment = new BookTutorFragment();
                fragment.setArguments(params);
                ((MainActivity)mContext).addStackedFragment(
                        mThisFragment,
                        fragment,
                        getString(R.string.title_book_tutor),
                        getString(R.string.title_tutor_profile));
            }
        });

        mBtnBookingReg = (Button) view.findViewById(R.id.btnBookingReg);
        mBtnBookingReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putInt(Constants.PARAM_KEY_ID_USER, mIdUser);
                params.putString(Constants.PARAM_KEY_NAME, mName);
                params.putDouble(Constants.PARAM_KEY_DISTANCE, mDistance);
                Fragment fragment = new BookRegularFragment();
                fragment.setArguments(params);
                ((MainActivity)mContext).addStackedFragment(
                        mThisFragment,
                        fragment,
                        getString(R.string.title_book_tutor_regular),
                        getString(R.string.title_tutor_profile));
            }
        });

        faveTutor = (RatingBar) view.findViewById(R.id.faveTutor);
        faveTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(faveTutor.getRating() == 1.0){
                    Toast.makeText(mContext, "Favorit", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext, "Tdk Favorit", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);

        mPagerAdapter = new TabsPagerAdapter(mContextActivity.getSupportFragmentManager());
        mPagerAdapter.addFragment(new TutorInfoProfileFragment(), getString(R.string.tutor_tab_title_profile));
        Bundle params = new Bundle();
        params.putInt(Constants.PARAM_KEY_ID_USER, mIdUser);
        Fragment secondFrag = new TutorInfoAvailableFragment();
        secondFrag.setArguments(params);
        mPagerAdapter.addFragment(secondFrag, getString(R.string.tutor_tab_title_availability));
        mPagerAdapter.addFragment(new TutorInfoFeedbackFragment(), getString(R.string.tutor_tab_title_feedback));
        pager.setAdapter(mPagerAdapter);
        tabs.setupWithViewPager(pager);

        downloadUserData();
        bindInitialData();
        return view;
    }

    private void bindInitialData() {
        Picasso.with(mContext).load(App.URL_PATH_PHOTO + mPhotoUrl)
                .transform(new RoundedCornersTransform())
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(mImgUserPhoto);
        mTextName.setText(mName);
        DecimalFormat df = new DecimalFormat("#.#");
        mTextLocation.setText(mLocationAddress + " - " + df.format(mDistance) + " km");
    }

    private void downloadUserData() {
        OkHttpClient httpClient = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_GET_TUTOR_INFO + "?id=" + mIdUser)
                .build();

        Call httpCall = httpClient.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful() && response.code() == 200) {
                    final String json = response.body().string();
                    try {
                        final JSONObject jsonObj = new JSONObject(json);
                        final JSONObject jsonData = jsonObj.getJSONObject("data");
                        final int minPrice = jsonData.getInt(Constants.PARAM_KEY_MIN_PRICE);
                        final int hasFinishedSess = jsonData.getInt("hasFinishedSession");
                        final String bio = jsonData.getString("bio");
                        final String email = jsonData.getString("email");
                        final String phone = jsonData.getString("phone");
                        final int availability = jsonData.getInt("is_available");
                        Type type = new TypeToken<Map<String, SavedSubject>>() {}.getType();
                        final Map<String, SavedSubject> subjectMap = new Gson().fromJson(jsonData.getString("subjects"), type);

                        Type eduType = new TypeToken<List<Education>>(){}.getType();
                        final List<Education> educationList = new Gson().fromJson(jsonData.getString("educations"), eduType);
                        mContextActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextPrice.setText("Rp. " + minPrice + " / Jam");
                                mTvHasFinishedSess.setText(hasFinishedSess + " sesi dilakukan");
                                ((TutorInfoProfileFragment)mPagerAdapter.getItem(0)).update(
                                        bio,
                                        email,
                                        phone,
                                        subjectMap,
                                        educationList
                                );
                                ((TutorInfoAvailableFragment)mPagerAdapter.getItem(1)).update(availability);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
