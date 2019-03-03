package com.example.sauravvishal8797.newsyfy.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

import com.example.sauravvishal8797.newsyfy.models.NewsArticleModel;
import com.example.sauravvishal8797.newsyfy.models.NewsResponseModel;
import com.example.sauravvishal8797.newsyfy.networking.ApiClient;
import com.example.sauravvishal8797.newsyfy.networking.ApiInterface;
import com.example.sauravvishal8797.newsyfy.utilities.Constants;
import com.example.sauravvishal8797.newsyfy.utilities.NotificationHelper;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationJobService extends JobService{

    @Override
    public boolean onStartJob(@NonNull JobParameters job) {
        retrieveTrendingNews();
        return false;
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters job) {
        return false;
    }

    private void retrieveTrendingNews(){
        Call<NewsResponseModel> call=null;
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryISO = telephonyManager.getNetworkCountryIso();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        call = apiService.getTopHeadLines(countryISO, Constants.RESULTS_PER_PAGE, 1, Constants.API_KEY);
        call.enqueue(new Callback<NewsResponseModel>() {
            @Override
            public void onResponse(Call<NewsResponseModel> call, Response<NewsResponseModel> response) {
                if (response!=null) {
                    ArrayList<NewsArticleModel> newsArticleModels = response.body().getmNewsArticleModels();
                    NewsArticleModel topNewsArticle = newsArticleModels.get(0);
                    String title = topNewsArticle.getmTitle();
                    String text = topNewsArticle.getmDescription();
                    String url = topNewsArticle.getmUrl();
                    String source = topNewsArticle.getNewsSourceModel().getmName();
                    showNotification(title, text, url, source);
                }
            }

            @Override
            public void onFailure(Call<NewsResponseModel> call, Throwable t) {

            }
        });
    }

    private void showNotification(String title, String text, String url, String source){
        NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
        notificationHelper.createNotificationChannel();
        notificationHelper.sendNotification(title, text, url, source);
    }
}
