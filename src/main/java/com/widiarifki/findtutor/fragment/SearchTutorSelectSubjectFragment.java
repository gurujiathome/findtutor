package com.widiarifki.findtutor.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.widiarifki.findtutor.MainActivity;
import com.widiarifki.findtutor.R;
import com.widiarifki.findtutor.adapter.SubjectSearchTutorAdapter;
import com.widiarifki.findtutor.app.App;
import com.widiarifki.findtutor.model.SavedSubject;
import com.widiarifki.findtutor.model.Subject;
import com.widiarifki.findtutor.model.SubjectCategory;
import com.widiarifki.findtutor.model.SubjectTopic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by widiarifki on 26/06/2017.
 */

public class SearchTutorSelectSubjectFragment extends Fragment {

    private Context mContext;
    private Activity mContextActivity;
    ArrayList<Subject> mSubjects;
    private HashMap<String, SubjectCategory> mSubjectCategoryMap;
    private HashMap<String, SavedSubject> mSavedSubjectMapTemp; // Variable list subjeck yg dipilih user + sub subject
    private HashMap<String, SavedSubject> mSavedSubjectMap; // Variable list subjeck yg dipilih user + sub subject
    private ArrayList<String> mSavedSubjectTopicList; // Variable topik/Sub subject yg dipilih

    private ListView mListViewSubject;
    private SubjectSearchTutorAdapter mAdapter;
    private EditText mSearchInput;
    ProgressDialog mProgressDialog;

    int mCheckedSubjectPos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSubjects = new ArrayList<Subject>();
        mSubjectCategoryMap = new HashMap<String, SubjectCategory>();
        mSavedSubjectMapTemp = new HashMap<String, SavedSubject>();
        mSavedSubjectTopicList = new ArrayList<String>();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.appbar_menu, menu);

        //menu.findItem(R.id.action_next).setVisible(true);
        menu.findItem(R.id.action_save).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_next) {
            ((MainActivity)mContext).addStackedFragment(new SearchTutorSelectLocationFragment(), getString(R.string.title_search_select_location), getString(R.string.title_search_select_subject));
        }
        else if(id == R.id.action_save){
            if(mSavedSubjectMap != null){
                mSavedSubjectMap.clear();
            }

            mSavedSubjectMap.putAll(mSavedSubjectMapTemp);
            ((MainActivity)mContext).removeFragmentFromStack(this);
        }
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        mContextActivity = (Activity)mContext;

        mSavedSubjectMap = ((MainActivity)mContext).getSearchTutorSubject();
        if(mSavedSubjectMap == null){
            ((MainActivity)mContext).setSearchTutorSubject(new HashMap<String, SavedSubject>());
            mSavedSubjectMap = ((MainActivity)mContext).getSearchTutorSubject();
        }else{
            for(SavedSubject object : mSavedSubjectMap.values()){
                HashMap<String, SubjectTopic> topicList = object.getTopicList();
                for(SubjectTopic topic : topicList.values()){
                    mSavedSubjectTopicList.add(topic.getId()+"");
                }
            }
        }

        View view = inflater.inflate(R.layout.fragment_search_tutor_select_subject, container, false);
        mListViewSubject = (ListView) view.findViewById(R.id.lvSubject);
        mListViewSubject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSavedSubjectMapTemp.clear();

                SubjectTopic subjectTopicItem = (SubjectTopic) view.getTag();
                int idCategory = subjectTopicItem.getIdCategory() == 0 ? subjectTopicItem.getId() : subjectTopicItem.getIdCategory();

                SubjectCategory subjectCategory = mSubjectCategoryMap.get(idCategory+"");
                HashMap<String, SubjectTopic> savedSubjectTopicMap = new HashMap<String, SubjectTopic>();
                savedSubjectTopicMap.put(subjectTopicItem.getId()+"", subjectTopicItem);
                mSavedSubjectMapTemp.put(idCategory+"", new SavedSubject(idCategory, subjectCategory.getName(), savedSubjectTopicMap));

                App.hideSoftKeyboard(mContext);
            }
        });
        mSearchInput = (EditText) view.findViewById(R.id.searchInput);
        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                mAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCancelable(true);

        fetchRefData();

        return view;
    }

    private void fetchRefData() {
        mProgressDialog.setMessage("Tunggu sebentar...");
        if(!mProgressDialog.isShowing()) mProgressDialog.show();

        OkHttpClient client = new OkHttpClient();
        Request httpRequest = new Request.Builder()
                .url(App.URL_GET_SUBJECT_LIST)
                .build();
        Call httpCall = client.newCall(httpRequest);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                if(response.isSuccessful() && response.code() == 200){
                    try {
                        JSONArray dataJson = new JSONArray(json);
                        int index = 0;
                        for (int i = 0; i < dataJson.length(); i++) {
                            JSONObject dataObj = dataJson.getJSONObject(i);
                            int idCategory = dataObj.getInt("FK_ID_PARENT");
                            int id = dataObj.getInt("ID_REF_SUBJECT");
                            String name = dataObj.getString("SUBJECT_NAME");

                            if(mSavedSubjectTopicList != null && !mSavedSubjectTopicList.isEmpty() && id == Integer.parseInt(mSavedSubjectTopicList.get(0))){
                                mCheckedSubjectPos = (idCategory == 0) ? index + 1 : index;
                            }

                            if(idCategory == 0){
                                mSubjects.add(new SubjectCategory(id, name));
                                mSubjects.add(new SubjectTopic(idCategory, id, name));
                                index += 2;
                            }else{
                                mSubjects.add(new SubjectTopic(idCategory, id, name));
                                index += 1;
                            }
                        }

                        for (Subject subject : mSubjects) {
                            if(subject.isSection()) mSubjectCategoryMap.put(subject.getId()+"", (SubjectCategory) subject);
                        }

                        /** Hide progress bae **/
                        mContextActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter = new SubjectSearchTutorAdapter(mContext, mSubjects);
                                mListViewSubject.setAdapter(mAdapter);
                                if(mSavedSubjectTopicList != null && !mSavedSubjectTopicList.isEmpty()){
                                    mListViewSubject.setItemChecked(mCheckedSubjectPos, true);
                                    mListViewSubject.setSelection(mCheckedSubjectPos);
                                }
                                if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{

                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        App.hideSoftKeyboard(mContext);
    }
}
