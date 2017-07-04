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
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.TabsPagerAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.helper.CircleTransform;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by widiarifki on 01/07/2017.
 */

public class TutorInfoFragment extends Fragment {

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);

        TabsPagerAdapter adapter = new TabsPagerAdapter(mContextActivity.getSupportFragmentManager());
        adapter.addFragment(new TutorInfoProfileFragment(), getString(R.string.tutor_tab_title_profile));
        adapter.addFragment(new TutorInfoSubjectFragment(), getString(R.string.tutor_tab_title_subject));
        adapter.addFragment(new TutorInfoFeedbackFragment(), getString(R.string.tutor_tab_title_feedback));
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

        downloadUserData();
        bindInitialData();
        return view;
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

            }
        });
    }

    private void bindInitialData() {
        Picasso.with(mContext).load(App.URL_PATH_PHOTO + mPhotoUrl)
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(mImgUserPhoto);
    }
}
