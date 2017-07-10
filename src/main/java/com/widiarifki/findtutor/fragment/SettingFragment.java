package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.WelcomeActivity;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.model.User;

/**
 * Created by widiarifki on 28/05/2017.
 */

public class SettingFragment extends Fragment {

    private Context mContext;
    private Activity mContextActivity;
    LinearLayout mBasicProfileMenu;
    LinearLayout mTutorPrefMenu;
    LinearLayout mChangeEmailMenu;
    LinearLayout mChangePassMenu;
    LinearLayout mAccountLogoutMenu;
    private SessionManager mSession;
    private User mUserLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        mContextActivity = (Activity)mContext;
        mSession = new SessionManager(mContext);
        mUserLogin = mSession.getUserDetail();

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mBasicProfileMenu = (LinearLayout) view.findViewById(R.id.basicProfile);
        mTutorPrefMenu = (LinearLayout) view.findViewById(R.id.tutorPref);
        mChangeEmailMenu = (LinearLayout) view.findViewById(R.id.changeEmail);
        mChangePassMenu = (LinearLayout) view.findViewById(R.id.changePassword);
        mAccountLogoutMenu = (LinearLayout) view.findViewById(R.id.accountLogout);

        final String stackedFragmentTitle = getString(R.string.menu_preferences);
        mBasicProfileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)mContext).addStackedFragment(new SettingProfileFragment(), getString(R.string.set_basic_profile), stackedFragmentTitle);
            }
        });

        mTutorPrefMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)mContext).addStackedFragment(new SettingTutorPrefFragment(), getString(R.string.set_tutor_preference), stackedFragmentTitle);
            }
        });

        mAccountLogoutMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage("Anda yakin akan logout dari akun anda?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent welcomeIntent = new Intent(mContext, WelcomeActivity.class);
                                startActivity(welcomeIntent);
                                mContextActivity.finish();
                                mSession.logout();
                            }
                        }).create().show();
            }
        });


        /*final String[] settingList = {
                ,
                getString(R.string.set_tutor_preference),
                getString(R.string.set_account),
        };
        final Fragment[] fragmentList = {
                new SettingProfileFragment(),
                new SettingTutorPrefFragment(),
                new SettingAccountFragment(),
        };

        ArrayAdapter mTabsPagerAdapter = new ArrayAdapter<String>(mContext, R.layout.item_layout_settings_list, settingList);

        ListView listView = (ListView) view.findViewById(R.id.settings_list);
        listView.setAdapter(mTabsPagerAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> mParentActivity, View view, int position, long id) {
                ((MainActivity)mContext).addStackedFragment(fragmentList[position], settingList[position], getString(R.string.menu_preferences));
            }
        });*/
        /** Hide by user role */
        if(mUserLogin.getIsTutor() == 1){
            mTutorPrefMenu.setVisibility(View.VISIBLE);
        }else{
            mTutorPrefMenu.setVisibility(View.GONE);
        }

        return view;
    }
}
