package com.widiarifki.findtutor.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.TabsPagerAdapter;
import com.widiarifki.findtutor.app.Constants;

/**
 * Created by widiarifki on 26/06/2017.
 */

public class SessionFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    Context mContext;
    Fragment mThisFragment;
    TabsPagerAdapter mTabsPagerAdapter;
    ViewPager mViewPager;
    TabLayout mTabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mThisFragment = this;
        mContext = container.getContext();

        View view = inflater.inflate(R.layout.fragment_session, container, false);

        TabLayout topTabs = (TabLayout) view.findViewById(R.id.topTabs);
        topTabs.addTab(topTabs.newTab().setText("Sebagai Tutor"));
        topTabs.addTab(topTabs.newTab().setText("Sebagai Siswa"));
        topTabs.setOnTabSelectedListener(this);

        /*final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).addStackedFragment(new SearchTutorSelectSubjectFragment(), getString(R.string.title_search_select_subject), getString(R.string.menu_my_session));
            }
        });*/
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupTabs(Constants.SESSION_CONTEXT_AS_TUTOR);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(tab.getPosition() == 0){
            setupTabs(Constants.SESSION_CONTEXT_AS_TUTOR);
        }
        else if(tab.getPosition() == 1){
            setupTabs(Constants.SESSION_CONTEXT_AS_STUDENT);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void setupTabs(int userContext) {
        if(mViewPager == null)
            mViewPager = (ViewPager) getView().findViewById(R.id.pager);
        if(mTabLayout == null)
            mTabLayout = (TabLayout) getView().findViewById(R.id.tabs);

        Bundle params = new Bundle();
        params.putInt(Constants.PARAM_KEY_USER_CONTEXT, userContext);

        Fragment sessionAccFragment = new SessionAcceptedFragment();
        sessionAccFragment.setArguments(params);

        Fragment sessionPendingFragment = new SessionPendingFragment();
        sessionPendingFragment.setArguments(params);

        if(mViewPager.getAdapter() == null) {
            mTabsPagerAdapter = new TabsPagerAdapter(this.getChildFragmentManager());
        }else{
            mTabsPagerAdapter.clearAll();
            mViewPager.setAdapter(null);
        }

        mTabsPagerAdapter.addFragment(sessionAccFragment, getString(R.string.session_accepted));
        mTabsPagerAdapter.addFragment(sessionPendingFragment, getString(R.string.session_pending));

        mViewPager.setAdapter(mTabsPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_check_box_white_24dp);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_hourglass_empty_white_24dp);
    }
}
