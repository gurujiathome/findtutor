package com.widiarifki.findtutor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.helper.SessionManager;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.Subject;
import com.widiarifki.findtutor.model.SubjectCategory;
import com.widiarifki.findtutor.model.SubjectTopic;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by widiarifki on 26/06/2017.
 */

public class SearchTutorSubjectAdapter extends BaseAdapter {
    private SessionManager mSession;
    private Context mContext;
    private ArrayList<Subject> mSubjects;
    private HashMap<String, SubjectCategory> mSubjectCategoryHashMap = new HashMap<String, SubjectCategory>();
    private HashMap<String, SavedSubject> mSavedSubject; // Variable list subjeck yg dipilih user + sub subject
    private ArrayList<String> mSavedSubjectTopic = new ArrayList<String>(); // Variable topik/Sub subject yg dipilih

    public SearchTutorSubjectAdapter() {
        super();
    }

    public SearchTutorSubjectAdapter(Context context, ArrayList<Subject> subjects) {
        this.mContext = context;
        this.mSubjects = subjects;
        mSession = new SessionManager(mContext);

        /** Add only category subject to HashMap **/
        for (Subject subject : mSubjects) {
            if(subject.isSection()) mSubjectCategoryHashMap.put(subject.getId()+"", (SubjectCategory) subject);
        }

        // Check if activity has saved Subject
        mSavedSubject = ((MainActivity)mContext).getSearchTutorSubject();
        if(mSavedSubject == null){
            /** Initialized User's saved subject **/
            mSavedSubject = new HashMap<String, SavedSubject>();
            ((MainActivity)mContext).setSearchTutorSubject(mSavedSubject);
        }else{
            for(SavedSubject object : mSavedSubject.values()){
                HashMap<String, SubjectTopic> topicList = object.getTopicList();
                for(SubjectTopic topic : topicList.values()){
                    mSavedSubjectTopic.add(topic.getId()+"");
                }
            }
        }
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
            // if section header
            convertView = inflater.inflate(R.layout.item_layout_list_header, parent, false);
            TextView tvSubjectCategory = (TextView) convertView.findViewById(R.id.text_title);

            SubjectCategory subjectCategory = (SubjectCategory) mSubjects.get(position);
            tvSubjectCategory.setText(subjectCategory.getName());
        }
        else
        {
            // if item
            SubjectTopic topic = (SubjectTopic) mSubjects.get(position);
            convertView = inflater.inflate(R.layout.item_layout_cbox_subject_topic, parent, false);
            CheckBox cbSubjectTopic = (CheckBox) convertView.findViewById(R.id.cbox_subject);
            cbSubjectTopic.setText(topic.getName());
            cbSubjectTopic.setTag(topic);
            cbSubjectTopic.setChecked(mSavedSubjectTopic.contains(topic.getId()+""));
            cbSubjectTopic.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    SubjectTopic item = (SubjectTopic) cb.getTag();
                    int idParent = item.getIdCategory();
                    int idSubject = idParent == 0 ? item.getId() : item.getIdCategory();

                    SavedSubject subjectGroup = mSavedSubject.get(idSubject+"");
                    HashMap<String, SubjectTopic> savedTopicList = new HashMap<String, SubjectTopic>();
                    if(cb.isChecked() == true){
                        if(subjectGroup != null){
                            savedTopicList = subjectGroup.getTopicList();
                            savedTopicList.put(item.getId()+"", item);
                        }else{
                            /** add category to hashmap for 1st time **/
                            SubjectCategory selectedCat = mSubjectCategoryHashMap.get(idSubject+"");
                            savedTopicList.put(item.getId()+"", item);
                            mSavedSubject.put(idSubject+"", new SavedSubject(idSubject, selectedCat.getName(), savedTopicList));
                        }
                    } else {
                        if(subjectGroup != null) {
                            savedTopicList = subjectGroup.getTopicList();
                            savedTopicList.remove(item.getId()+"");
                            if (savedTopicList.isEmpty())
                                mSavedSubject.remove(idSubject+"");
                        }
                    }
                }
            });
        }

        return convertView;
    }
}
