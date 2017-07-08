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
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.TabsPagerAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.helper.CircleTransform;

import org.json.JSONException;
import org.json.JSONObject;

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
    String mRequestedDate;

    ImageView mImgUserPhoto;
    TextView mTextName;
    TextView mTextPrice;
    TextView mTextLocation;
    Button mBtnBooking;

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
        mRequestedDate = params.getString("date", null);
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
        mBtnBooking = (Button) view.findViewById(R.id.btnBooking);
        mBtnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putString("date", mRequestedDate);
                params.putInt(Constants.PARAM_KEY_ID_USER, mIdUser);
                Fragment fragment = new BookTutorFragment();
                fragment.setArguments(params);
                ((MainActivity)mContext).addStackedFragment(
                        mThisFragment,
                        fragment,
                        getString(R.string.title_book_tutor),
                        getString(R.string.title_tutor_profile));
            }
        });

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
                if(response.isSuccessful() && response.code() == 200) {
                    String json = response.body().string();
                    try {
                        final JSONObject jsonObj = new JSONObject(json);
                        mContextActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void bindInitialData() {
        mTextName.setText(mName);
        mTextLocation.setText(mLocationAddress + " - " + mDistance);
        Picasso.with(mContext).load(App.URL_PATH_PHOTO + mPhotoUrl)
                .transform(new CircleTransform())
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(mImgUserPhoto);
    }
}
