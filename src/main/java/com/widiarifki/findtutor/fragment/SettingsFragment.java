package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;

/**
 * Created by widiarifki on 28/05/2017.
 */

public class SettingsFragment extends Fragment {

    private Context mContext;
    private Activity mContextActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        final String[] settingList = {
                getString(R.string.set_basic_profile),
                getString(R.string.set_tutor_preference),
                getString(R.string.set_account),
        };
        final Fragment[] fragmentList = {
                new SettingProfileFragment(),
                new SettingTutorPrefFragment(),
                new SettingAccountFragment(),
        };

        ArrayAdapter adapter = new ArrayAdapter<String>(mContext, R.layout.item_layout_settings_list, settingList);

        ListView listView = (ListView) view.findViewById(R.id.settings_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)mContext).addStackedFragment(fragmentList[position], settingList[position], getString(R.string.menu_preferences));
            }
        });

        return view;
    }
}
