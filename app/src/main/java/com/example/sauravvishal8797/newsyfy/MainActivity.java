package com.example.sauravvishal8797.newsyfy;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.sauravvishal8797.newsyfy.adapters.NewsAdapter;
import com.example.sauravvishal8797.newsyfy.models.NewsArticleModel;
import com.example.sauravvishal8797.newsyfy.models.NewsResponseModel;
import com.example.sauravvishal8797.newsyfy.networking.ApiClient;
import com.example.sauravvishal8797.newsyfy.networking.ApiInterface;
import com.example.sauravvishal8797.newsyfy.utilities.Constants;

import java.util.ArrayList;

import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mNewsDisplayView;
    private ProgressBar mProgressBar;

    //handles on-click action for adapter items
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            NewsArticleModel newsArticleModel = (NewsArticleModel)view.findViewById(R.id.article_title).getTag();
            Intent intent = new Intent(MainActivity.this, NewsActivity.class);
            intent.putExtra("url", newsArticleModel.getmUrl());
            intent.putExtra("source", newsArticleModel.getNewsSourceModel().getmName());
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTitle(getResources().getString(R.string.app_name));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            toolbar.setTitleMargin(20, 20, 0, 0);
        }
        setUpUI();
    }

    /**Sets up the UI for displaying news articles*/
    private void setUpUI(){
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryISO = telephonyManager.getNetworkCountryIso();
        mNewsDisplayView = findViewById(R.id.news_display_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mNewsDisplayView.setLayoutManager(linearLayoutManager);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setClickable(false);
        fetchArticlesFromApi(countryISO);
    }

    /** makes retrofit calls to the api to fetch news data */
    private void fetchArticlesFromApi(String countryISO){
        final ArrayList<NewsArticleModel>[] articles = new ArrayList[]{new ArrayList<>()};
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<NewsResponseModel> call = apiService.getTopHeadLines(countryISO, Constants.API_KEY);
        call.enqueue(new Callback<NewsResponseModel>() {
            @Override
            public void onResponse(retrofit2.Call<NewsResponseModel> call, Response<NewsResponseModel> response) {
                articles[0] = response.body().getmNewsArticleModels();
                if (articles[0].size()!=0){
                    mProgressBar.setVisibility(View.GONE);
                    ArrayList<NewsArticleModel> articleModels = articles[0];
                    NewsAdapter newsAdapter = new NewsAdapter(getApplicationContext(), articleModels);
                    newsAdapter.setOnClickListener(mOnClickListener);
                    mNewsDisplayView.setAdapter(newsAdapter);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<NewsResponseModel> call, Throwable t) {
                //to be used later
            }
        });
    }
}
