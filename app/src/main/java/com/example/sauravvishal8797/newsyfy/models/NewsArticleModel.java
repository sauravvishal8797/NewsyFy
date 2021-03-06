package com.example.sauravvishal8797.newsyfy.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsArticleModel implements Comparable{

    @SerializedName("source")
    @Expose
    private NewsSourceModel newsSourceModel;
    @SerializedName("author")
    @Expose
    private String mAuthor;
    @SerializedName("title")
    @Expose
    private String mTitle;
    @SerializedName("description")
    @Expose
    private String mDescription;
    @SerializedName("url")
    @Expose
    private String mUrl;
    @SerializedName("urlToImage")
    @Expose
    private String mUrlToImage;
    @SerializedName("publishedAt")
    @Expose
    private String mPublishTime;
    @SerializedName("content")
    @Expose
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

    /**compareTo method of the comparable interface to compare two date objects i.e the current object
     * and the object passed as a parameter
     * @param o
     * @return
     */
    @Override
    public int compareTo(@NonNull Object o) {
        Date first = null, second = null;
        String publishTime = ((NewsArticleModel)o).getmPublishTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            first = simpleDateFormat.parse(publishTime);
            second = simpleDateFormat.parse(mPublishTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return first.compareTo(second);
    }
}
