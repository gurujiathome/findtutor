package com.widiarifki.findtutor.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.EducationListAdapter;
import com.widiarifki.findtutor.adapter.SubjectListAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.model.Education;
import com.widiarifki.findtutor.model.SavedSubject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Created by widiarifki on 04/07/2017.
 */

public class TutorInfoProfileFragment extends Fragment {

    private Context mContext;

    String mBio;
    List<Education> mEducations;
    HashMap<String, SavedSubject> mSavedSubject;
    EducationListAdapter mEduListAdapter;
    SubjectListAdapter mSubjectListAdapter;

    TextView mTvBio;
    TextView mTvPhone;
    TextView mTvEmail;
    ListView mLvSubject;
    ListView mLvEdu;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mContext = getContext();

        mEducations = new ArrayList<Education>();
        mEduListAdapter = new EducationListAdapter(mContext, mEducations);

        mSavedSubject = new HashMap<String, SavedSubject>();

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutor_info_profile, container, false);

        mTvBio = (TextView) view.findViewById(R.id.tvBio);
        mTvPhone = (TextView) view.findViewById(R.id.tvPhone);
        mTvEmail = (TextView) view.findViewById(R.id.tvBio);

        mLvSubject = (ListView) view.findViewById(R.id.lvSubject);
        mLvSubject.setAdapter(mSubjectListAdapter);

        mLvEdu = (ListView) view.findViewById(R.id.lvEdu);
        mLvEdu.setAdapter(mEduListAdapter);

        return view;
    }

    public void update(String bio, String email, String phone, Map<String, SavedSubject> subjects, List<Education> educations) {
        mTvBio.setText(bio);
        mTvPhone.setText(phone);
        mTvEmail.setText(email);

        if(subjects!=null) {
            if (subjects.size() > 0) {
                mSavedSubject = new HashMap<String, SavedSubject>(subjects);
                mSubjectListAdapter = new SubjectListAdapter(mContext, mSavedSubject);
                mLvSubject.setAdapter(mSubjectListAdapter);
                mSubjectListAdapter.notifyDataSetChanged();
                App.setListViewHeightBasedOnChildren(mLvSubject);
            }
        }

        mEducations.clear();
        mEduListAdapter.notifyDataSetChanged();
        if(educations!=null) {
            if (educations.size() > 0) {
                mEducations.addAll(educations);
                mEduListAdapter.notifyDataSetChanged();
                App.setListViewHeightBasedOnChildren(mLvEdu);
            }
        }
    }
}
