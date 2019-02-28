package com.example.sauravvishal8797.newsyfy.networking;

import com.example.sauravvishal8797.newsyfy.models.NewsResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    //end-point to retrive top-news headlines
    @GET("top-headlines")
    Call<NewsResponseModel> getTopHeadLines(@Query("country") String country, @Query("apiKey") String apiKey);

    //end-point to retrive category-wise top-news headlines
    @GET("top-headlines")
    Call<NewsResponseModel> getTopHeadlinesCategoryWise(@Query("country") String country, @Query("category") String category,
                                                        @Query("apiKey") String apiKey);
}
