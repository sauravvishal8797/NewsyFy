package com.example.sauravvishal8797.newsyfy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.sauravvishal8797.newsyfy.models.SourceInfo;
import com.example.sauravvishal8797.newsyfy.models.SourceResponseModel;
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

    //to store all the news source IDs
    private String[] sourceIds;

    //to store all the news source names
    private String[] sourceNames;

    //to retrieve the country code
    private TelephonyManager telephonyManager;
    private String countryISO;

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

        //retrieving the country code
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        countryISO = telephonyManager.getNetworkCountryIso();

        getAllSources();
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
                displayProgressBar(true);
                getTopHeadLines();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.filter_by_source:
                displayDialog(sourceIds, sourceNames);
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
                dialogInterface.dismiss();
            }
        });

        builder.setPositiveButton(getResources().getString(R.string.ok_button_text).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
            newsAdapter.swapDataSet(new ArrayList<NewsArticleModel>());
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
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
                    displayProgressBar(false);
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
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<NewsResponseModel> call = apiInterface.getItemsWithSearchWord(searchKeyword, Constants.API_KEY);
        call.enqueue(new Callback<NewsResponseModel>() {
            @Override
            public void onResponse(Call<NewsResponseModel> call, Response<NewsResponseModel> response) {
                if (response!=null) {
                    articles[0] = response.body().getmNewsArticleModels();
                    displayProgressBar(false);
                    newsAdapter.swapDataSet(articles[0]);
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
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
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
     * @return a list of articles from the specified sources
     */
    private void getSourceFilteredArticles(String sources){
        final ArrayList<NewsArticleModel>[] articles = new ArrayList[]{new ArrayList<>()};
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<NewsResponseModel> call = apiInterface.getHeadLinesFromSources(sources, Constants.API_KEY);
        call.enqueue(new Callback<NewsResponseModel>() {
            @Override
            public void onResponse(Call<NewsResponseModel> call, Response<NewsResponseModel> response) {
                if (response.isSuccessful()){
                    articles[0] = response.body().getmNewsArticleModels();
                    displayProgressBar(false);
                    newsAdapter.swapDataSet(articles[0]);
                }
            }

            @Override
            public void onFailure(Call<NewsResponseModel> call, Throwable t) {

            }
        });
    }
}
