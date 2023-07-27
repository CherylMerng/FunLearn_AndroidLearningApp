package com.example.facialrecognitionandroidprototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WatchVideoActivity extends AppCompatActivity {

    private String mUrl;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if ((keyCode == KeyEvent.KEYCODE_BACK)){
            Intent intent = new Intent(this, MenuActivity.class);
            finish();
            startActivity(intent);
            return true;
        }
        return true;
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest resourceRequest){
            if ("www.youtube.com".equals(resourceRequest.getUrl().getHost())) {
                return true;
            }
            Uri logout = Uri.parse(getResources().getString(R.string.HOST_ADDRESS) + "login");
            if (resourceRequest.getUrl().equals(logout)) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                return true;
            }
            Uri menu = Uri.parse(getResources().getString(R.string.HOST_ADDRESS) + "child/menu");
            if (resourceRequest.getUrl().equals(menu)){
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");

        // Initialize webview and launch the url
        mWebView = findViewById(R.id.web_view);
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