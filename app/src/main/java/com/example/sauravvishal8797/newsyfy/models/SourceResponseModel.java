package com.example.sauravvishal8797.newsyfy.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SourceResponseModel {

    @SerializedName("status")
    @Expose
    private String mStatus;
    @SerializedName("sources")
    @Expose
    private ArrayList<SourceInfo> mSourceInfo;

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmSourceInfo(ArrayList<SourceInfo> mSourceInfo) {
        this.mSourceInfo = mSourceInfo;
    }

    public ArrayList<SourceInfo> getmSourceInfo() {
        return mSourceInfo;
    }
}
