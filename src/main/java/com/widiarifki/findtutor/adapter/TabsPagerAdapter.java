package com.widiarifki.findtutor.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by widiarifki on 27/05/2017.
 */

public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    FragmentManager mFragmentManager;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mTitleList = new ArrayList<>();

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mTitleList.add(title);
    }

    public void clearAll() //Clear all page
    {
        for(int i=0; i<mFragmentList.size(); i++)
            mFragmentManager.beginTransaction().remove(mFragmentList.get(i)).commit();
        mFragmentList.clear();
    }
}
