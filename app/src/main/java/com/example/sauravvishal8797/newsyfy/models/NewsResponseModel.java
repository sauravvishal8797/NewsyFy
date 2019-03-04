package com.example.sauravvishal8797.newsyfy.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NewsResponseModel {

    @SerializedName("status")
    private String mStatus;
    @SerializedName("totalResults")
    private int mTotalResults;
    @SerializedName("articles")
    private ArrayList<NewsArticleModel> mNewsArticleModels;

    public NewsResponseModel(){}

    public NewsResponseModel(String mStatus, int mTotalResults, ArrayList<NewsArticleModel> mNewsArticleModels){
        this.mStatus = mStatus;
        this.mTotalResults = mTotalResults;
        this.mNewsArticleModels = mNewsArticleModels;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmTotalResults(int mTotalResults) {
        this.mTotalResults = mTotalResults;
    }

    public int getmTotalResults() {
        return mTotalResults;
    }

    public void setmNewsArticleModels(ArrayList<NewsArticleModel> mNewsArticleModels) {
        this.mNewsArticleModels = mNewsArticleModels;
    }

    public ArrayList<NewsArticleModel> getmNewsArticleModels() {
        return mNewsArticleModels;
    }
}
