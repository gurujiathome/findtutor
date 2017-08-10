package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.Constants;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.SubjectTopic;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by widiarifki on 30/06/2017.
 */

public class SearchTutorFragment extends Fragment {

    Fragment mThisFragment;
    Context mContext;
    Activity mContextActivity;
    MainActivity mParentActivity;

    TextView mTvSubject;
    TextView mTvLocation;
    TextView mTvDate;
    ImageButton mBtnClearDate;
    TextView mTvGender;
    Button mBtnSearch;

    int mSelectedD = 0;
    int mSelectedM = 0;
    int mSelectedY = 0;
    int mSelectedGender;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mThisFragment = this;
        mContext = container.getContext();
        mContextActivity = (Activity)mContext;
        mParentActivity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_search_tutor, container, false);

        mTvSubject = (TextView) view.findViewById(R.id.tvSubject);
        mTvLocation = (TextView) view.findViewById(R.id.tvLocation);
        mTvDate = (TextView) view.findViewById(R.id.tvDate);
        mBtnClearDate = (ImageButton) view.findViewById(R.id.btn_clear_date);
        mTvGender = (TextView) view.findViewById(R.id.tvGender);
        mBtnSearch = (Button) view.findViewById(R.id.btnSearchTutor);
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mParentActivity.addStackedFragment(new SearchTutorResultFragment(), getString(R.string.title_search_tutor_result), getString(R.string.title_search_tutor));
                Bundle params = new Bundle();
                params.putInt(Constants.PARAM_KEY_GENDER, mSelectedGender);
                Fragment fragment = new SearchTutorResultFragment();
                //Fragment fragment = new TestSlideFragment();
                fragment.setArguments(params);
                mParentActivity.addStackedFragment(mThisFragment, fragment, getString(R.string.title_search_tutor_result), getString(R.string.title_search_tutor));
            }
        });

        LinearLayout selectSubject = (LinearLayout) view.findViewById(R.id.selectSubject);
        selectSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentActivity.addStackedFragment(new SearchTutorSelectSubjectFragment(), getString(R.string.title_search_select_subject), getString(R.string.title_search_tutor));
                //mParentActivity.addStackedFragment(mThisFragment, new SearchTutorSelectSubjectFragment(), getString(R.string.title_search_select_subject), getString(R.string.title_search_tutor));
            }
        });

        LinearLayout selectLocation = (LinearLayout) view.findViewById(R.id.selectLocation);
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentActivity.addStackedFragment(new SearchTutorSelectLocationFragment(), getString(R.string.title_search_select_location), getString(R.string.title_search_tutor));
                //mParentActivity.addStackedFragment(mThisFragment, new SearchTutorSelectLocationFragment(), getString(R.string.title_search_select_location), getString(R.string.title_search_tutor));
            }
        });

        final LinearLayout selectDate = (LinearLayout) view.findViewById(R.id.selectDate);
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = mSelectedY;
                int month = mSelectedM - 1;
                int day = mSelectedD;
                if(mSelectedD == 0 && mSelectedM == 0 && mSelectedY == 0){
                    final Calendar c = Calendar.getInstance();
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                }

                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_select_date, null);
                final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
                datePicker.init(year, month, day, null);
                datePicker.setMinDate(System.currentTimeMillis() - 1000);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialogBuilder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedD = datePicker.getDayOfMonth();
                        mSelectedM = datePicker.getMonth() + 1;
                        mSelectedY = datePicker.getYear();
                        mParentActivity.mSearchTutorDate = new LocalDate(mSelectedY, mSelectedM, mSelectedD);
                        updateTvDate();
                        /*LocalDate date = new LocalDate(mSelectedY, mSelectedM, mSelectedD);
                        String dateStr = date.toString("dd MM yyy");
                        mTvDate.setText(dateStr);*/
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                if(!alertDialog.isShowing()) alertDialog.show();
            }
        });

        mBtnClearDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentActivity.mSearchTutorDate = null;
                updateTvDate();
            }
        });

        LinearLayout selectGender = (LinearLayout) view.findViewById(R.id.selectGender);
        selectGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_layout_select_gender, null);
                final RadioGroup rgrupGender = (RadioGroup) dialogView.findViewById(R.id.rgrup_gender);
                if(mSelectedGender == 1) rgrupGender.check(R.id.radio_opt_male);
                if(mSelectedGender == 2) rgrupGender.check(R.id.radio_opt_male);
                if(mSelectedGender == 0) rgrupGender.check(R.id.radio_opt_no_gender);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Gender Preference (Optional)");
                builder.setView(dialogView);
                builder.setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String genderStr = "";
                        int selectedGender = rgrupGender.getCheckedRadioButtonId();
                        if(selectedGender == R.id.radio_opt_male){
                            genderStr = getString(R.string.label_opt_male);
                            mSelectedGender = 1;
                        }
                        else if(selectedGender == R.id.radio_opt_female){
                            genderStr = getString(R.string.label_opt_female);
                            mSelectedGender = 2;
                        }
                        else{
                            genderStr = getString(R.string.title_search_select_gender);
                            mSelectedGender = 0;
                        }
                        mTvGender.setText(genderStr);
                    }
                });

                AlertDialog alertDialog = builder.create();
                if(!alertDialog.isShowing()) alertDialog.show();
            }
        });

        bindInitialView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void bindInitialView() {
        HashMap<String, SavedSubject> subjects = ((MainActivity)mContext).getSearchTutorSubject();
        List<String> subjectDisplay = new ArrayList<String>();
        if(subjects != null){
            if(!subjects.isEmpty()){
                for (SavedSubject group : subjects.values()){
                    int idParent = group.getCategoryId();
                    String parentName = group.getCategoryName();
                    HashMap<String, SubjectTopic> topic = group.getTopicList();
                    if(topic.containsKey(idParent+"") && !subjectDisplay.contains(parentName)){
                        subjectDisplay.add(parentName);
                    }else{
                        for (SubjectTopic topicItem : topic.values())
                            subjectDisplay.add(topicItem.getName());
                    }
                }
            }
        }

        if(!subjectDisplay.isEmpty()){
            String[] subjectDispArray = subjectDisplay.toArray(new String[0]);
            mTvSubject.setText(TextUtils.join(", ", subjectDispArray));
        }

        String location = ((MainActivity) mContext).mSearchTutorLocationTxt;
        if(location != null)
            mTvLocation.setText(location);

        updateTvDate();
    }

    private void updateTvDate() {
        LocalDate date = mParentActivity.mSearchTutorDate;
        if(date != null) {
            String dateStr = "";
            if(date.equals(new LocalDate())) {
                dateStr += "Hari ini";
            }
            else {
                dateStr += Constants.DAY_INA[Integer.parseInt(date.toString("e")) - 1];
            }
            dateStr += ", ";
            dateStr += date.toString("dd MMM yyy");

            mTvDate.setText(dateStr);
            mBtnClearDate.setVisibility(View.VISIBLE);
        }else{
            mTvDate.setText(getString(R.string.title_search_select_date));
            mBtnClearDate.setVisibility(View.GONE);
        }
    }
}
