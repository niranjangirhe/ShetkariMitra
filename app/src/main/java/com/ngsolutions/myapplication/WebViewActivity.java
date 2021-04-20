package com.ngsolutions.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.monstertechno.adblocker.AdBlockerWebView;
import com.monstertechno.adblocker.util.AdBlocker;

import java.util.Locale;

public class WebViewActivity extends AppCompatActivity {
    WebView webview;
    String url;
    String pageTitle;
    TextView pageTitleText;
    ImageButton backButton,backInWeb;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        loadLocale();
        pageTitleText = findViewById(R.id.PageTitleText);
        webview = (WebView) findViewById(R.id.WebViewForAll);
        new AdBlockerWebView.init(this).initializeWebView(webview);

        backButton = findViewById(R.id.BackBtnWebView);
        backInWeb = findViewById(R.id.BackInWeb);
        progressBar = findViewById(R.id.PageLoadProgreess);
        progressBar.setVisibility(View.INVISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openHomeActivity = new Intent(WebViewActivity.this, HomePage.class);
                openHomeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openHomeActivity, 0);
                finish();
            }
        });
        backInWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(webview.canGoBack())
                    webview.goBack();
                else {
                    finish();
                }
            }
        });



        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(false);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        Intent intent= getIntent();
        Bundle b = intent.getExtras();

        if(b!=null)
        {
            url =(String) b.get("url");
            SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
            String lang = prefs.getString("My_lang","");
            if(!lang.contains("en"))
            {
                url = "https://translate.google.com/translate?sl=en&tl=" + lang + "&u=" + url;
            }
            pageTitle =(String) b.get("pageTitle");
            webview.setWebViewClient(new Browser_home() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    return super.shouldOverrideUrlLoading(view, request);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    progressBar.setVisibility(View.INVISIBLE);
                    super.onPageFinished(view, url);
                }
            });


            webview.loadUrl(url);
            Log.d("niranjanFromWebView",url);
            Log.d("niranjanFromWebView",pageTitle);
            pageTitleText.setText(pageTitle);
        }

    }
    private class Browser_home extends WebViewClient {

        Browser_home() {}

        @SuppressWarnings("deprecation")
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

            return AdBlockerWebView.blockAds(view,url) ? AdBlocker.createEmptyResource() :
                    super.shouldInterceptRequest(view, url);

        }

    }
    private void setLocale(String lang)
    {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_lang",lang);
        editor.apply();
    }
    private  void loadLocale()
    {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_lang","en");
        setLocale(lang);
    }
}