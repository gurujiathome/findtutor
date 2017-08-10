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

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.TabsPagerAdapter;

/**
 * Created by widiarifki on 05/08/2017.
 */

public class TestSlideFragment extends Fragment {

    private AppCompatActivity mContextActivity;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mContextActivity = (AppCompatActivity) mContext;

        View view = inflater.inflate(R.layout.test_slide_fragment, container, false);

        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);

        TabsPagerAdapter adapter = new TabsPagerAdapter(mContextActivity.getSupportFragmentManager());
        adapter.addFragment(new TutorInfoProfileFragment(), getString(R.string.tutor_tab_title_profile));
        adapter.addFragment(new TutorInfoSubjectFragment(), getString(R.string.tutor_tab_title_subject));
        adapter.addFragment(new TutorInfoFeedbackFragment(), getString(R.string.tutor_tab_title_feedback));
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

        return view;
    }
}
