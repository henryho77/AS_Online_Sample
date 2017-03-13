package com.disp_tech.dispbbs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Knuckles on 2015/12/2.
 */
public class TextActivity extends AppCompatActivity {
    //TextView urlTextView;
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 設定這個頁面XML的layout名稱
        setContentView(R.layout.activity_text);

        // 設定要顯示回上一頁的按鈕
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 取得從 Intent 傳來的資料，改成文章網址存為 url
        Bundle args = this.getIntent().getExtras();
        String url = "http://disp.cc/m/" + args.getString("bi") + "-" + args.getString("ti");

        // 取得XML中的TextView，設定文字為 url
        //urlTextView = (TextView) findViewById(R.id.url_textview);
        //urlTextView.setText(url);

        // 取得XML中的WebView
        mWebView = (WebView) findViewById(R.id.webview);

        // WebView的設定選項
        WebSettings webSettings = mWebView.getSettings();
        // Enable Javascript
        webSettings.setJavaScriptEnabled(true);
        // Enable LocalStorage
        webSettings.setDomStorageEnabled(true);
        //webSettings.setAllowUniversalAccessFromFileURLs(true);

        // 加這行以避免跳出APP用瀏覽器開啟
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());

        // 載入網址
        mWebView.loadUrl(url);

    }
}
