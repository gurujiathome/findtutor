package com.widiarifki.findtutor.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.AvailabilityAdapter;
import com.widiarifki.findtutor.model.Day;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by widiarifki on 09/06/2017.
 */

public class SettingAvailabilityFragment extends Fragment {

    private Context mContext;
    private List<Day> mDays;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();

        mDays = new ArrayList<Day>();
        mDays.add(new Day(0, "Senin"));
        mDays.add(new Day(1, "Selasa"));
        mDays.add(new Day(2, "Rabu"));
        mDays.add(new Day(3, "Kamis"));
        mDays.add(new Day(4, "Jumat"));
        mDays.add(new Day(5, "Sabtu"));
        mDays.add(new Day(6, "Minggu"));

        View view = inflater.inflate(R.layout.fragment_setting_availability, container, false);
        final ListView lvDays = (ListView) view.findViewById(R.id.lvDays);
        lvDays.setAdapter(new AvailabilityAdapter(mContext, mDays));
        lvDays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Day day = mDays.get(position);
                Fragment fragment = new SetTimeAvailabilityFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("idDay", day.getId());
                fragment.setArguments(bundle);
                ((MainActivity)getContext()).addStackedFragment(fragment, "Jadwal Hari " + day.getName(), getString(R.string.menu_availibility));
            }
        });
        return view;
    }
}
