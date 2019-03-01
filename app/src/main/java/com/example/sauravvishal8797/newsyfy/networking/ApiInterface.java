package com.example.sauravvishal8797.newsyfy.networking;

import com.example.sauravvishal8797.newsyfy.models.NewsResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    //retrieves top-news headlines
    @GET("top-headlines")
    Call<NewsResponseModel> getTopHeadLines(@Query("country") String country, @Query("apiKey") String apiKey);

    //retrives category-wise top-news headlines
    @GET("top-headlines")
    Call<NewsResponseModel> getTopHeadlinesCategoryWise(@Query("country") String country, @Query("category") String category,
                                                        @Query("apiKey") String apiKey);

    //Returns all the newsArticles containing the search query
    @GET("everything")
    Call<NewsResponseModel> getItemsWithSearchWord(@Query("q") String queryString, @Query("apiKey") String apiKey);

}
