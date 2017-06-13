package com.widiarifki.findtutor.model;

/**
 * Created by widiarifki on 05/06/2017.
 */
import java.util.HashMap;

public class SavedSubject {
    private int mCategoryId;
    private String mCategoryName;
    private HashMap<String, SubjectTopic> mTopicList;

    public SavedSubject(){
        super();
    }

    public SavedSubject(int categoryId, String categoryName, HashMap<String, SubjectTopic> topicList){
        mCategoryId = categoryId;
        mCategoryName = categoryName;
        mTopicList = topicList;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(int categoryId) {
        this.mCategoryId = categoryId;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(String categoryName) {
        this.mCategoryName = categoryName;
    }

    public HashMap<String, SubjectTopic> getTopicList() {
        return mTopicList;
    }

    public void setTopicList(HashMap<String, SubjectTopic> topicList) {
        this.mTopicList = topicList;
    }
}
