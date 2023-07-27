package com.example.facialrecognitionandroidprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import kotlin.jvm.internal.Intrinsics;

public class LoginActivity extends AppCompatActivity {

    private String mUrl;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    public void onBackPressed () {

    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest resourceRequest){
            Uri child = Uri.parse(getResources().getString(R.string.HOST_ADDRESS) + "child/menu");
            Uri parent = Uri.parse(getResources().getString(R.string.HOST_ADDRESS) + "parent/menu");
            Uri admin = Uri.parse(getResources().getString(R.string.HOST_ADDRESS) + "admin/menu");
            Uri register = Uri.parse(getResources().getString(R.string.HOST_ADDRESS) + "registeraccount");
            if (resourceRequest.getUrl().equals(child)){
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
                return true;
            }
            if (resourceRequest.getUrl().equals(parent) || resourceRequest.getUrl().equals(admin)){
                mWebView.loadUrl(mUrl);
                Toast toast = Toast.makeText(getApplicationContext(), "Please use the website for your activities instead. This mobile application is only for child users." , Toast.LENGTH_LONG);
                toast.show();
                return true;
            }
            if (resourceRequest.getUrl().equals(register)){
                Toast toast = Toast.makeText(getApplicationContext(), "Register Account service is not available for mobile application. Please use the website instead. " , Toast.LENGTH_LONG);
                toast.show();
                return true;

            }
        return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.logo);
        mUrl = getResources().getString(R.string.HOST_ADDRESS);

        // Initialize webview and launch the url
        mWebView = findViewById(R.id.web_view);
        //can fit the webpage to screen size but it causes the page to be very small, need to configure the scale.
//        mWebView.getSettings().setLoadWithOverviewMode(true);
//        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(true);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setSafeBrowsingEnabled(false);
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setMax(100);
        mWebView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView webView, int newProgress){
                if (newProgress == 100){
                    mProgressBar.setVisibility(View.GONE);
                }
                else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }
        });

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(mUrl);
    }

    }