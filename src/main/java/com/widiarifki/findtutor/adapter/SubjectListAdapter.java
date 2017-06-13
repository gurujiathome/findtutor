package com.widiarifki.findtutor.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.SubjectTopic;

import java.util.HashMap;

/**
 * Created by widiarifki on 05/06/2017.
 */

public class SubjectListAdapter extends BaseAdapter {

    private Context mContext;
    private HashMap<String, SavedSubject> mData = new HashMap<String, SavedSubject>();
    private String[] mKeys;

    public SubjectListAdapter(Context context, HashMap<String, SavedSubject> data){
        mContext = context;
        mData  = data;
        if(mData == null)
            mKeys = new String[0];
        else
            mKeys = mData.keySet().toArray(new String[data.size()]);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public SavedSubject getItem(int position) {
        return mData.get(mKeys[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout_subject_list, null);
        }

        String key = mKeys[position];
        SavedSubject value = getItem(position);
        if(value != null){
            TextView subjectParent = (TextView) convertView.findViewById(R.id.text_subject_parent);
            TextView subjectTopic = (TextView) convertView.findViewById(R.id.text_subject_topic);

            int idParent = value.getCategoryId();
            HashMap<String, SubjectTopic> topicList = value.getTopicList();
            String topicString = "";
            if(topicList.get(idParent+"") == null){
                /** If user not checked 'Semua' **/
                String[] topicArrayString = new String[topicList.size()];
                int i = 0;
                for (SubjectTopic topic : topicList.values()) {
                    topicArrayString[i] = topic.getName();
                    i++;
                }
                topicString = TextUtils.join(", ", topicArrayString);
            }else{
                topicString = "Semua topik " + value.getCategoryName();
            }

            // Bind UI w data
            subjectParent.setText(value.getCategoryName());
            subjectTopic.setText(topicString);
        }

        return convertView;
    }
}
