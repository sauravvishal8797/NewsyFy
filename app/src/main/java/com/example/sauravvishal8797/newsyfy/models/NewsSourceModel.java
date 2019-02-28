package com.example.sauravvishal8797.newsyfy.models;

import com.google.gson.annotations.SerializedName;

public class NewsSourceModel {

    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;

    public NewsSourceModel(String id, String name){
        mId = id;
        mName = name;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmId() {
        return mId;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmName() {
        return mName;
    }
}
