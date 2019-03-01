package com.example.sauravvishal8797.newsyfy;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.sauravvishal8797.newsyfy.adapters.NewsAdapter;
import com.example.sauravvishal8797.newsyfy.models.NewsArticleModel;
import com.example.sauravvishal8797.newsyfy.models.NewsResponseModel;
import com.example.sauravvishal8797.newsyfy.networking.ApiClient;
import com.example.sauravvishal8797.newsyfy.networking.ApiInterface;
import com.example.sauravvishal8797.newsyfy.utilities.Constants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mNewsDisplayView;
    private ProgressBar mProgressBar;
    private NewsAdapter newsAdapter;

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
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        //getSupportActionBar().
        setUpUI();
    }

    /**Sets up the UI for displaying news articles*/
    private void setUpUI() {
        mNewsDisplayView = findViewById(R.id.news_display_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mNewsDisplayView.setLayoutManager(linearLayoutManager);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setClickable(false);
        getTopHeadLines();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        MenuItem item = menu.findItem(R.id.searchView);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(final View view, boolean b) {
                if (b) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context
                                    .INPUT_METHOD_SERVICE);
                            imm.showSoftInput(view.findFocus(), 0);
                        }
                    }, 200);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context
                            .INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mProgressBar.setVisibility(View.VISIBLE);
                newsAdapter.swapDataSet(new ArrayList<NewsArticleModel>());
                getSearchedArticles(s);
                return true;
            }
        });
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                mProgressBar.setVisibility(View.VISIBLE);
                getTopHeadLines();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.searchView:

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * retrieves top news articles based on location
     * @return List of news Articles to display
     */
    public void getTopHeadLines(){
        Call<NewsResponseModel> call=null;
        final ArrayList<NewsArticleModel>[] articles = new ArrayList[]{new ArrayList<>()};
        final ArrayList<NewsArticleModel> newsArticles = new ArrayList<>();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryISO = telephonyManager.getNetworkCountryIso();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        call = apiService.getTopHeadLines(countryISO, Constants.API_KEY);
        call.enqueue(new Callback<NewsResponseModel>() {
            @Override
            public void onResponse(Call<NewsResponseModel> call, Response<NewsResponseModel> response) {
                if (response!=null) {
                    articles[0] = response.body().getmNewsArticleModels();
                    mProgressBar.setVisibility(View.GONE);
                    newsAdapter = new NewsAdapter(getApplicationContext(), articles[0]);
                    newsAdapter.setOnClickListener(mOnClickListener);
                    mNewsDisplayView.setAdapter(newsAdapter);
                }

            }

            @Override
            public void onFailure(Call<NewsResponseModel> call, Throwable t) {

            }
        });
    }

    /**
     * retrieves news articles based on some search query
     * @return List of news articles
     */
    public void getSearchedArticles(String searchKeyword){
        final ArrayList<NewsArticleModel>[] articles = new ArrayList[]{new ArrayList<>()};
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String countryISO = telephonyManager.getNetworkCountryIso();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<NewsResponseModel> call = apiInterface.getItemsWithSearchWord(searchKeyword, Constants.API_KEY);
        call.enqueue(new Callback<NewsResponseModel>() {
            @Override
            public void onResponse(Call<NewsResponseModel> call, Response<NewsResponseModel> response) {
                if (response!=null) {
                    articles[0] = response.body().getmNewsArticleModels();
                    mProgressBar.setVisibility(View.GONE);
                    newsAdapter.swapDataSet(articles[0]);
                }
            }

            @Override
            public void onFailure(Call<NewsResponseModel> call, Throwable t) {

            }
        });
    }
}
