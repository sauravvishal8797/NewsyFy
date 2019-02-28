package com.example.sauravvishal8797.newsyfy.models;

import com.google.gson.annotations.SerializedName;

public class NewsArticleModel {

    @SerializedName("source")
    private NewsSourceModel newsSourceModel;
    @SerializedName("author")
    private String mAuthor;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("url")
    private String mUrl;
    @SerializedName("urlToImage")
    private String mUrlToImage;
    @SerializedName("publishedAt")
    private String mPublishTime;
    @SerializedName("content")
    private String mContent;

    public NewsArticleModel(NewsSourceModel newsSourceModel, String author, String title, String description,
                            String url, String mUrlToImage, String mPublishTime, String mContent){
        this.newsSourceModel = newsSourceModel;
        this.mAuthor = author;
        this.mTitle = title;
        this.mDescription = description;
        this.mUrl = url;
        this.mUrlToImage = mUrlToImage;
        this.mPublishTime = mPublishTime;
        this.mContent = mContent;
    }

    public void setNewsSourceModel(NewsSourceModel newsSourceModel) {
        this.newsSourceModel = newsSourceModel;
    }

    public NewsSourceModel getNewsSourceModel() {
        return newsSourceModel;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrlToImage(String mUrlToImage) {
        this.mUrlToImage = mUrlToImage;
    }

    public String getmUrlToImage() {
        return mUrlToImage;
    }

    public void setmPublishTime(String mPublishTime) {
        this.mPublishTime = mPublishTime;
    }

    public String getmPublishTime() {
        return mPublishTime;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getmContent() {
        return mContent;
    }
}
