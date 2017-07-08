package com.widiarifki.findtutor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.model.Education;

import java.util.List;

/**
 * Created by widiarifki on 01/06/2017.
 */

public class EducationListAdapter extends ArrayAdapter<Education> {

    private Context mContext;
    private List<Education> mListEducation;

    public EducationListAdapter(@NonNull Context context, List<Education> objects) {
        super(context, 0, objects);
        mContext = context;
        mListEducation = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_education, null);
        }

        //get the property we are displaying
        Education education = getItem(position);
        if(education != null){
            TextView year = (TextView) convertView.findViewById(R.id.text_year);
            TextView schoolName = (TextView) convertView.findViewById(R.id.text_school_name);
            TextView deptLevel = (TextView) convertView.findViewById(R.id.text_dept_level);

            year.setText(education.getYearGraduate());
            schoolName.setText(education.getSchoolName());
            deptLevel.setText(education.getSchoolLevelText() + "-" + education.getDepartment());
        }

        return convertView;
    }

    public void addToList(Education education){
        mListEducation.add(education);
        notifyDataSetChanged();
    }
}
