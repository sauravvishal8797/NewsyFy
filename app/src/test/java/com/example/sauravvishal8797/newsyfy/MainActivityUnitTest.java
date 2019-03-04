package com.example.sauravvishal8797.newsyfy;

import com.example.sauravvishal8797.newsyfy.adapters.NewsAdapter;
import com.example.sauravvishal8797.newsyfy.models.NewsArticleModel;
import com.example.sauravvishal8797.newsyfy.models.NewsResponseModel;
import com.example.sauravvishal8797.newsyfy.models.SourceResponseModel;
import com.example.sauravvishal8797.newsyfy.networking.ApiInterface;
import com.example.sauravvishal8797.newsyfy.utilities.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityUnitTest {

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getTopHeadlinesTest(){
        ApiInterface mockedApiInterface = Mockito.mock(ApiInterface.class);
        final Call<NewsResponseModel> mockedCall = Mockito.mock(Call.class);
        Mockito.when(mockedApiInterface.getTopHeadLines("in", Constants.RESULTS_PER_PAGE, 1,
                Constants.API_KEY)).thenReturn(mockedCall);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<NewsResponseModel> callback = invocation.getArgumentAt(0, Callback.class);
                callback.onResponse(mockedCall, Response.success(new NewsResponseModel()));
                return null;
            }
        }).when(mockedCall).enqueue(any(Callback.class));
        List<NewsArticleModel> articles = Mockito.mock(List.class);
        NewsAdapter newsAdapter = Mockito.mock(NewsAdapter.class);
        newsAdapter.swapDataSet((ArrayList<NewsArticleModel>) articles);
    }

    @Test
    public void getSearchedArticlesTest(){
        ApiInterface mockedApiInterface = Mockito.mock(ApiInterface.class);
        final Call<NewsResponseModel> mockedCall = Mockito.mock(Call.class);
        Mockito.when(mockedApiInterface.getItemsWithSearchWord("cricket", Constants.RESULTS_PER_PAGE, 1,
                Constants.API_KEY)).thenReturn(mockedCall);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<NewsResponseModel> callback = invocation.getArgumentAt(0, Callback.class);
                callback.onResponse(mockedCall, Response.success(new NewsResponseModel()));
                return null;
            }
        }).when(mockedCall).enqueue(any(Callback.class));
        List<NewsArticleModel> articles = Mockito.mock(List.class);
        NewsAdapter newsAdapter = Mockito.mock(NewsAdapter.class);
        newsAdapter.swapDataSet((ArrayList<NewsArticleModel>) articles);
    }

    @Test
    public void getAllSources(){
        ApiInterface mockedApiInterface = Mockito.mock(ApiInterface.class);
        final Call<SourceResponseModel> mockedCall = Mockito.mock(Call.class);
        Mockito.when(mockedApiInterface.getAllTheSources(Constants.API_KEY)).thenReturn(mockedCall);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<SourceResponseModel> callback = invocation.getArgumentAt(0, Callback.class);
                callback.onResponse(mockedCall, Response.success(new SourceResponseModel()));
                return null;
            }
        }).when(mockedCall).enqueue(any(Callback.class));
    }

    @Test
    public void getSourceFilteredArticleTest(){
        ApiInterface mockedApiInterface = Mockito.mock(ApiInterface.class);
        final Call<NewsResponseModel> mockedCall = Mockito.mock(Call.class);
        Mockito.when(mockedApiInterface.getHeadLinesFromSources("bbc-news, abc-news", Constants.RESULTS_PER_PAGE, 1,
                Constants.API_KEY)).thenReturn(mockedCall);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Callback<NewsResponseModel> callback = invocation.getArgumentAt(0, Callback.class);
                callback.onResponse(mockedCall, Response.success(new NewsResponseModel()));
                return null;
            }
        }).when(mockedCall).enqueue(any(Callback.class));
        List<NewsArticleModel> articles = Mockito.mock(List.class);
        NewsAdapter newsAdapter = Mockito.mock(NewsAdapter.class);
        newsAdapter.swapDataSet((ArrayList<NewsArticleModel>) articles);
    }
}
