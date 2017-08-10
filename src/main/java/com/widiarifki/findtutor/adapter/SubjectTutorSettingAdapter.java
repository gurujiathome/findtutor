package com.widiarifki.findtutor.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;

import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.app.SessionManager;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.Subject;
import com.widiarifki.findtutor.model.SubjectCategory;
import com.widiarifki.findtutor.model.SubjectTopic;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by widiarifki on 04/06/2017.
 */

public class SubjectTutorSettingAdapter extends ArrayAdapter<Subject> {

    private SessionManager mSession;
    private Context mContext;
    private ArrayList<Subject> mSubjects;
    private ArrayList<Subject> mSubjectsOri;
    // Hashmap kan data subjek yg merupakan parent/kategori saja
    private HashMap<String, SubjectCategory> mSubjectCategoryMap = new HashMap<String, SubjectCategory>();
    // data subjek tersimpan
    private HashMap<String, SavedSubject> mSavedSubjectMap;
    // data subjek tersimpan (topik)
    private ArrayList<String> mSavedSubjectTopicList = new ArrayList<String>();

    ProgressDialog mProgressDialog;

    public SubjectTutorSettingAdapter(@NonNull Context context, ArrayList<Subject> objects) {
        super(context, 0, objects);
        mContext = context;
        mSession = new SessionManager(mContext);
        mSubjects = new ArrayList<Subject>();
        mSubjects.addAll(objects);
        mSubjectsOri = new ArrayList<Subject>();
        mSubjectsOri.addAll(objects);
        for (Subject subject : mSubjects) {
            if(subject.isSection()) mSubjectCategoryMap.put(subject.getId()+"", (SubjectCategory) subject);
        }
        // Check if activity has saved Subject
        mSavedSubjectMap = ((MainActivity)mContext).getSelectedSubject();
        for(SavedSubject object : mSavedSubjectMap.values()){
            for(SubjectTopic subjectTopic : object.getTopicList().values()){
                mSavedSubjectTopicList.add(subjectTopic.getId()+"");
            }
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        Subject subject = getItem(position);
        if(subject != null) {
            if (subject.isSection()) {
                // if section header
                convertView = inflater.inflate(R.layout.item_list_default_header, null);
                TextView tvSubjectCategory = (TextView) convertView.findViewById(R.id.text_title);

                SubjectCategory subjectCategory = (SubjectCategory) subject;
                tvSubjectCategory.setText(subjectCategory.getName());
            } else {
                // if item
                convertView = inflater.inflate(R.layout.item_list_select_subject, null);
                CheckBox cbSubjectTopic = (CheckBox) convertView.findViewById(R.id.cbox_subject);

                SubjectTopic subjectTopic = (SubjectTopic) subject;
                String label = (subjectTopic.getIdCategory() == 0) ? "Semua topik di " + subjectTopic.getName() : subjectTopic.getName();
                cbSubjectTopic.setTag(subjectTopic);
                cbSubjectTopic.setText(label);
                cbSubjectTopic.setChecked(mSavedSubjectTopicList.contains(subjectTopic.getId() + ""));
                cbSubjectTopic.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox checkBox = (CheckBox) v;
                        SubjectTopic subjectTopicItem = (SubjectTopic) checkBox.getTag();
                        int idCategory = subjectTopicItem.getIdCategory() == 0 ? subjectTopicItem.getId() : subjectTopicItem.getIdCategory();

                        SavedSubject savedSubjectGroup = mSavedSubjectMap.get(idCategory + "");
                        HashMap<String, SubjectTopic> savedTopicList = new HashMap<String, SubjectTopic>();
                        if (checkBox.isChecked() == true) {
                            if (savedSubjectGroup != null) {
                                savedTopicList = savedSubjectGroup.getTopicList();
                                savedTopicList.put(subjectTopicItem.getId() + "", subjectTopicItem);
                            } else {
                                /** add category to hashmap for 1st time **/
                                SubjectCategory subjectCategory = mSubjectCategoryMap.get(idCategory + "");
                                savedTopicList.put(subjectTopicItem.getId() + "", subjectTopicItem);

                                SavedSubject newSubjectGroup = new SavedSubject(idCategory, subjectCategory.getName(), savedTopicList);
                                mSavedSubjectMap.put(idCategory + "", newSubjectGroup);
                            }

                            if (((MainActivity) mContext).getSelectedSubject().isEmpty()) {
                                // Update saved subject for the first time
                                ((MainActivity) mContext).setSelectedSubject(mSavedSubjectMap);
                            }

                            App.hideSoftKeyboard(mContext);
                        } else {
                            if (savedSubjectGroup != null) {
                                savedTopicList = savedSubjectGroup.getTopicList();
                                savedTopicList.remove(subjectTopicItem.getId() + "");
                                if (savedTopicList.isEmpty())
                                    mSavedSubjectMap.remove(idCategory + "");
                            }

                            App.hideSoftKeyboard(mContext);
                        }
                    }
                });
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

/*public class SubjectTutorSettingAdapter extends BaseAdapter {
    private SessionManager mSession;
    private Context mContext;
    // data master subjek
    private ArrayList<Subject> mSubjects;
    // CAST - data master subjek
    private HashMap<String, SubjectCategory> mSubjectCategoryMap = new HashMap<String, SubjectCategory>(); // Variable list data subject yg di HASHMAP kan
    // data subjek tersimpan
    private HashMap<String, SavedSubject> mSavedSubjectMap;
    // data subjek tersimpan (topik)
    private ArrayList<String> mSavedSubjectTopicList = new ArrayList<String>();

    public SubjectTutorSettingAdapter() {
        super();
    }

    public SubjectTutorSettingAdapter(Context context, ArrayList<Subject> subjects) {
        this.mContext = context;
        this.mSubjects = subjects;
        mSession = new SessionManager(mContext);

        /** Add only category subject to HashMap **/
        /*for (Subject subject : mSubjects) {
            if(subject.isSection()) mSubjectCategoryMap.put(subject.getId()+"", (SubjectCategory) subject);
        }

        // Check if activity has saved Subject
        mSavedSubjectMap = ((MainActivity)mContext).getSelectedSubject();
        for(SavedSubject object : mSavedSubjectMap.values()){
            HashMap<String, SubjectTopic> topicList = object.getTopicList();
            for(SubjectTopic topic : topicList.values()){
                mSavedSubjectTopicList.add(topic.getId()+"");
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
            convertView = inflater.inflate(R.layout.item_list_default_header, parent, false);
            TextView tvSubjectCategory = (TextView) convertView.findViewById(R.id.text_title);

            SubjectCategory subjectCategory = (SubjectCategory) mSubjects.get(position);
            tvSubjectCategory.setText(subjectCategory.getName());
        }
        else
        {
            // if item
            SubjectTopic topic = (SubjectTopic) mSubjects.get(position);
            convertView = inflater.inflate(R.layout.item_list_select_subject, parent, false);
            CheckBox cbSubjectTopic = (CheckBox) convertView.findViewById(R.id.cbox_subject);
            cbSubjectTopic.setText(topic.getName());
            cbSubjectTopic.setTag(topic);
            cbSubjectTopic.setChecked(mSavedSubjectTopicList.contains(topic.getId()+""));
            cbSubjectTopic.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    SubjectTopic item = (SubjectTopic) cb.getTag();
                    int idParent = item.getIdCategory();
                    int idSubject = idParent == 0 ? item.getId() : item.getIdCategory();

                    SavedSubject subjectGroup = mSavedSubjectMap.get(idSubject+"");
                    HashMap<String, SubjectTopic> savedTopicList = new HashMap<String, SubjectTopic>();
                    if(cb.isChecked() == true){
                        if(subjectGroup != null){
                            savedTopicList = subjectGroup.getTopicList();
                            savedTopicList.put(item.getId()+"", item);
                        }else{
                            /** add category to hashmap for 1st time **/
                            /*SubjectCategory selectedCat = mSubjectCategoryMap.get(idSubject+"");
                            savedTopicList.put(item.getId()+"", item);
                            mSavedSubjectMap.put(idSubject+"", new SavedSubject(idSubject, selectedCat.getName(), savedTopicList));
                        }

                        if(((MainActivity)mContext).getSelectedSubject().isEmpty()){
                            // Update saved subject for the first time
                            ((MainActivity)mContext).setSelectedSubject(mSavedSubjectMap);
                        }
                    } else {
                        if(subjectGroup != null) {
                            savedTopicList = subjectGroup.getTopicList();
                            savedTopicList.remove(item.getId()+"");
                            if (savedTopicList.isEmpty())
                                mSavedSubjectMap.remove(idSubject+"");
                        }
                    }
                }
            });
        }

        return convertView;
    }
}*/
