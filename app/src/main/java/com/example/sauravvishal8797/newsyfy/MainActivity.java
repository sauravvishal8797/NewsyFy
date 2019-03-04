package com.example.sauravvishal8797.newsyfy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.sauravvishal8797.newsyfy.adapters.NewsAdapter;
import com.example.sauravvishal8797.newsyfy.models.NewsArticleModel;
import com.example.sauravvishal8797.newsyfy.models.NewsResponseModel;
import com.example.sauravvishal8797.newsyfy.models.SourceInfo;
import com.example.sauravvishal8797.newsyfy.models.SourceResponseModel;
import com.example.sauravvishal8797.newsyfy.networking.ApiClient;
import com.example.sauravvishal8797.newsyfy.networking.ApiInterface;
import com.example.sauravvishal8797.newsyfy.utilities.Constants;
import com.example.sauravvishal8797.newsyfy.utilities.NotificationJobScheduler;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mNewsDisplayView;
    private ProgressBar mProgressBar;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //to store/display the news articles retrieved
    private ArrayList<NewsArticleModel> newsDataList;

    //to store all the news source IDs
    private String[] sourceIds;

    //to keep track of the no of pages to display the news feed
    private int pageNo = 1;

    //to keep track of the total no of results to be returned by the api
    private int totalResults = 0;

    //to keep track whether results are being loaded
    private boolean isLoading = false;

    //to keep track whether loading is being performed on the basis of some search query
    private boolean isSearchLoading = false;

    //to keep track whether source filtering is on
    private boolean isSourceFiltering = false;

    //storing search query for particular search
    private String searchQuery = " ";

    //to store all the news source names
    private String[] sourceNames;

    //to retrieve the country code
    private TelephonyManager telephonyManager;
    private String countryISO;

    //handles on-click action for adapter items
    private View.OnClickListener mOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            NewsArticleModel newsArticleModel = (NewsArticleModel)view.findViewById(R.id.article_title).getTag();
            Intent intent = new Intent(MainActivity.this, NewsActivity.class);
            intent.putExtra("url", newsArticleModel.getmUrl());
            intent.putExtra("source", newsArticleModel.getNewsSourceModel().getmName());
            startActivity(intent);
        }
    };

    //handles onSwipe refresh configurations
    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            pageNo = 1;
            if (isSearchLoading){
                getSearchedArticles(searchQuery);
            } else {
                getTopHeadLines(pageNo);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Trending news notification job scheduler
        NotificationJobScheduler notificationJobScheduler = new NotificationJobScheduler(this);
        notificationJobScheduler.scheduleJob();

        newsDataList = new ArrayList<>();
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        //retrieving the country code
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        countryISO = telephonyManager.getNetworkCountryIso();

        getAllSources();
        setUpUI();
    }

    /**Sets up the UI for displaying news articles*/
    private void setUpUI() {
        mSwipeRefreshLayout = findViewById(R.id.onSwipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(refreshListener);
        mNewsDisplayView = findViewById(R.id.news_display_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mNewsDisplayView.setLayoutManager(linearLayoutManager);
        newsAdapter = new NewsAdapter(this, newsDataList);
        newsAdapter.setOnClickListener(mOnClickListener);
        mNewsDisplayView.setAdapter(newsAdapter);
        initScrollListener(linearLayoutManager);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setClickable(false);
        getTopHeadLines(pageNo);
    }

    private void initScrollListener(final LinearLayoutManager layoutManager){
        mNewsDisplayView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading){
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        int item = totalResults / pageNo;
                        if (item > Constants.RESULTS_PER_PAGE) {
                            loadMore(isSearchLoading);
                            isLoading = true;
                        }
                    }
                }
            }
        });
    }

    private void loadMore(boolean isSearchLoading){
        if (isSearchLoading){
            mProgressBar.setVisibility(View.VISIBLE);
            pageNo++;
            getSearchedArticles(searchQuery);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            pageNo++;
            getTopHeadLines(pageNo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);
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
                searchQuery = s;
                isSearchLoading = true;
                if (!s.isEmpty()) {
                    displayProgressBar(true);
                    getSearchedArticles(s);
                }
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
                isSearchLoading = false;
                if (!searchQuery.isEmpty()){
                    Log.i("sattire", searchQuery);
                    displayProgressBar(true);
                    getTopHeadLines(pageNo);
                }

                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.filter_by_source:
                pageNo=1;
                displayDialog(sourceIds, sourceNames);
                return true;

            case R.id.filter_by_date:
                 if (item.isChecked()){
                     Log.i("checked", "yes");
                     item.setChecked(false);
                 } else {
                     Log.i("checked", "no");
                     item.setChecked(true);
                     Collections.sort(newsDataList);
                     newsAdapter.swapDataSet(newsDataList);
                 }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayDialog(final String[] sourceIds, String[] sourceNames){
        final ArrayList<String> selectedSources = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.source_select_dialog_title));
        builder.setMultiChoiceItems(sourceNames, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b)
                    selectedSources.add(sourceIds[i]);
                else selectedSources.remove(sourceIds[i]);
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel_button_text).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isSourceFiltering = false;
                dialogInterface.dismiss();
            }
        });

        builder.setPositiveButton(getResources().getString(R.string.ok_button_text).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isSourceFiltering = true;
                StringBuilder stringBuilder = new StringBuilder();
                dialogInterface.dismiss();
                for (String id: selectedSources){
                    stringBuilder.append(id+",");
                }
                displayProgressBar(true);
                getSourceFilteredArticles(stringBuilder.toString().substring(0, stringBuilder.toString().length()));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayProgressBar(boolean toDisplay){
        if (toDisplay){
            mSwipeRefreshLayout.setEnabled(false);
            newsDataList = new ArrayList<>();
            newsAdapter.swapDataSet(newsDataList);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mSwipeRefreshLayout.setEnabled(true);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * retrieves top news articles based on location
     * @param pageNo
     * @return List of news Articles to display
     */
    public void getTopHeadLines(int pageNo){
        Call<NewsResponseModel> call=null;
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        countryISO = telephonyManager.getNetworkCountryIso();
        ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);
        call = apiService.getTopHeadLines(countryISO, Constants.RESULTS_PER_PAGE, pageNo, Constants.API_KEY);
        call.enqueue(new Callback<NewsResponseModel>() {
            @Override
            public void onResponse(Call<NewsResponseModel> call, Response<NewsResponseModel> response) {
                if (response!=null) {
                    if (isLoading)
                        newsDataList.addAll(response.body().getmNewsArticleModels());
                    else newsDataList = response.body().getmNewsArticleModels();
                    totalResults = response.body().getmTotalResults();
                    displayProgressBar(false);
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    newsAdapter.swapDataSet(newsDataList);
                    isLoading = false;
                }
            }

            @Override
            public void onFailure(Call<NewsResponseModel> call, Throwable t) {

            }
        });
    }

    /**
     * retrieves news articles based on some search query
     * @param searchKeyword
     * @return List of news articles
     */
    public void getSearchedArticles(String searchKeyword){
        isSearchLoading = true;
        ApiInterface apiInterface = ApiClient.getClient(this).create(ApiInterface.class);
        Call<NewsResponseModel> call = apiInterface.getItemsWithSearchWord(searchKeyword, Constants.RESULTS_PER_PAGE, pageNo,
                Constants.API_KEY);
        call.enqueue(new Callback<NewsResponseModel>() {
            @Override
            public void onResponse(Call<NewsResponseModel> call, Response<NewsResponseModel> response) {
                if (response!=null) {
                    if (isLoading)
                        newsDataList.addAll(response.body().getmNewsArticleModels());
                    else newsDataList = response.body().getmNewsArticleModels();
                    displayProgressBar(false);
                    mSwipeRefreshLayout.setRefreshing(false);
                    newsAdapter.swapDataSet(newsDataList);
                    isLoading = false;
                }
            }

            @Override
            public void onFailure(Call<NewsResponseModel> call, Throwable t) {

            }
        });
    }

    /**
     * retrieves all the available news sources
     * @return a list containing all the sources names and ids
     */
    private void getAllSources(){
        ApiInterface apiInterface = ApiClient.getClient(this).create(ApiInterface.class);
        Call<SourceResponseModel> call = apiInterface.getAllTheSources(Constants.API_KEY);
        call.enqueue(new Callback<SourceResponseModel>() {
            @Override
            public void onResponse(Call<SourceResponseModel> call, Response<SourceResponseModel> response) {
                if (response.isSuccessful()){
                    ArrayList<SourceInfo> sources = response.body().getmSourceInfo();
                    sourceIds = new String[sources.size()];
                    sourceNames = new String[sources.size()];
                    for (int i=0; i<sources.size(); i++){
                        sourceIds[i] = sources.get(i).getmId();
                        sourceNames[i] = sources.get(i).getmName();
                    }
                }
            }

            @Override
            public void onFailure(Call<SourceResponseModel> call, Throwable t) {

            }
        });
    }

    /**filters articles based on sources
     * @param sources String to filter the news acc to sources
     * @return a list of articles from the specified sources
     */
    private void getSourceFilteredArticles(String sources){
        ApiInterface apiInterface = ApiClient.getClient(this).create(ApiInterface.class);
        Call<NewsResponseModel> call = apiInterface.getHeadLinesFromSources(sources, Constants.RESULTS_PER_PAGE, pageNo,
                Constants.API_KEY);
        call.enqueue(new Callback<NewsResponseModel>() {
            @Override
            public void onResponse(Call<NewsResponseModel> call, Response<NewsResponseModel> response) {
                if (response.isSuccessful()){
                    if (isLoading)
                        newsDataList.addAll(response.body().getmNewsArticleModels());
                    else newsDataList = response.body().getmNewsArticleModels();
                    displayProgressBar(false);
                    newsAdapter.swapDataSet(newsDataList);
                    isLoading = false;
                }
            }

            @Override
            public void onFailure(Call<NewsResponseModel> call, Throwable t) {

            }
        });
    }
}
