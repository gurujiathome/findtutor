package com.widiarifki.findtutor.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;

/**
 * Created by widiarifki on 26/06/2017.
 */

public class MySessionFragment extends Fragment {

    Context mContext;
    Fragment mThisFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mThisFragment = this;
        mContext = container.getContext();
        View view = inflater.inflate(R.layout.fragment_my_session, container, false);

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)mContext).addStackedFragment(new SearchTutorSelectSubjectFragment(), getString(R.string.title_search_select_subject), getString(R.string.menu_my_session));
            }
        });
        return view;
    }
}
