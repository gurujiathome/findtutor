package com.widiarifki.findtutor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.TextView;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.model.Subject;
import com.widiarifki.findtutor.model.SubjectCategory;
import com.widiarifki.findtutor.model.SubjectTopic;

import java.util.ArrayList;

/**
 * Created by widiarifki on 26/06/2017.
 */

public class SubjectSearchTutorAdapter extends ArrayAdapter<Subject> {

    private Context mContext;
    private ArrayList<Subject> mSubjects;
    private ArrayList<Subject> mSubjectsOri;

    public SubjectSearchTutorAdapter(@NonNull Context context, ArrayList<Subject> subjects) {
        super(context, 0, subjects);
        mContext = context;
        mSubjects = new ArrayList<Subject>();
        mSubjects.addAll(subjects);
        mSubjectsOri = new ArrayList<Subject>();
        mSubjectsOri.addAll(subjects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        Subject subject = getItem(position);
        if(subject != null) {
            if (subject.isSection()) {
                convertView = inflater.inflate(R.layout.item_list_default_header, null);
                TextView tvSubjectCategory = (TextView) convertView.findViewById(R.id.text_title);

                SubjectCategory subjectCategory = (SubjectCategory) subject;
                tvSubjectCategory.setText(subjectCategory.getName());
            }
            else
            {
                SubjectTopic subjectTopic = (SubjectTopic) subject;
                convertView = inflater.inflate(R.layout.item_list_select_subject_radio, null);
                convertView.setTag(subjectTopic);

                String label = subjectTopic.getIdCategory() == 0 ? "Semua topik " + subjectTopic.getName() : subjectTopic.getName();
                CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(R.id.rbtn_subject);
                checkedTextView.setText(label);
            }
        }else{
            convertView = inflater.inflate(R.layout.item_list_default_header, null);
        }
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null && constraint.toString().length() > 0) {
                    ArrayList<Subject> tempList = new ArrayList<Subject>();
                    for (int i = 0; i < mSubjectsOri.size(); i++){
                        Subject subjectItem = mSubjectsOri.get(i);
                        String subjectName = subjectItem.getName().toLowerCase();
                        String searchKeyword = constraint.toString().toLowerCase();
                        if(subjectName.contains(searchKeyword))
                            tempList.add(subjectItem);
                    }

                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }else{
                    filterResults.values = mSubjectsOri;
                    filterResults.count = mSubjectsOri.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mSubjects = (ArrayList<Subject>) results.values;
                notifyDataSetChanged();
                clear();
                for(int i = 0; i < mSubjects.size(); i++)
                    add(mSubjects.get(i));
                notifyDataSetInvalidated();
            }
        };
    }
}

/*public class SubjectSearchTutorAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Subject> mSubjects;

    public SubjectSearchTutorAdapter(Context context, ArrayList<Subject> subjects) {
        mContext = context;
        mSubjects = subjects;
    }

    @Override
    public int getCount() {
        return mSubjects.size();
    }

    @Override
    public Object getItem(int position) {
        return mSubjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mSubjects.get(position).isSection()) {
            convertView = inflater.inflate(R.layout.item_list_default_header, parent, false);
            TextView tvSubjectCategory = (TextView) convertView.findViewById(R.id.text_title);

            SubjectCategory subjectCategory = (SubjectCategory) mSubjects.get(position);
            tvSubjectCategory.setText(subjectCategory.getName());
        }
        else
        {
            SubjectTopic topic = (SubjectTopic) mSubjects.get(position);
            convertView = inflater.inflate(R.layout.item_list_select_subject_radio, parent, false);
            convertView.setTag(topic);

            String label = topic.getIdCategory() == 0 ? "Semua topik " + topic.getName() : topic.getName();
            CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(R.id.rbtn_subject);
            checkedTextView.setText(label);
        }

        return convertView;
    }
}*/
