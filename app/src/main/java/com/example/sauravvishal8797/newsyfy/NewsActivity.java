package com.example.sauravvishal8797.newsyfy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toolbar;

public class NewsActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_news);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(intent.getStringExtra("source"));
        webView = findViewById(R.id.news_web_view);
        progressBar = findViewById(R.id.webview_progress_bar);
        displayProgressBar(true);
        initWebView();
        loadUrl(intent.getStringExtra("url"));
    }

    public void loadUrl(String url){
        webView.loadUrl(url);
    }

    public void displayProgressBar(boolean toDisplay){
        if (toDisplay){
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

    private void initWebView(){
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                displayProgressBar(false);
                invalidateOptionsMenu();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                displayProgressBar(false);
                invalidateOptionsMenu();
            }
        });
    }
}
