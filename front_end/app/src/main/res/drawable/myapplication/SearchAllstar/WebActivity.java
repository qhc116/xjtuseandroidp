package com.example.myapplication.SearchAllstar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.material.navigation.NavigationView;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class WebActivity extends AppCompatActivity {
    WebView mWebView;
    WebSettings mWebSettings;
    Intent intent;
    String[] shits;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_web);
        mWebView = findViewById(R.id.wv);
        intent = getIntent();
        String name = intent.getStringExtra("name");
        shits = name.split(",");
        goUrl(shits);
        Log.d("shits:",shits.toString());
        initWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);                    //  支持Javascript 与js交互
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//  支持通过JS打开新窗口
        mWebSettings.setAllowFileAccess(true);                      //  设置可以访问文件
        mWebSettings.setSupportZoom(true);                          //  支持缩放
        mWebSettings.setBuiltInZoomControls(true);                  //  设置内置的缩放控件
        mWebSettings.setUseWideViewPort(true);                      //  自适应屏幕
        mWebSettings.setSupportMultipleWindows(true);               //  多窗口
        mWebSettings.setDefaultTextEncodingName("utf-8");           //  设置编码格式
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);       //  缓存模式
        //设置不用系统浏览器打开,直接显示在当前WebView
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith("bilibili:")){
                    view.loadUrl(url);
                }
                    return true;
            }
        });
        //设置WebChromeClient类
        //mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl("https://search.bilibili.com/all?keyword="+ shits[1] +"&from_source=nav_search_new&order=totalrank&duration=0&tids_1=119");
    }

    @Override
    protected void onStop() {
        // 清理缓存
        if (mWebView != null) {
            mWebView.loadUrl("about:blank");
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            mWebView.destroy();
            mWebView = null;
        }
        super.onStop();
    }

    private void goUrl(String []name ){
        //获得长度
        int len=name.length;
        navigationView=findViewById(R.id.nav_left);
        //动态创建侧边的item
        for (int i=1;i<len;i++){
            navigationView.getMenu().add(i,i,i,name[i]);
            MyonClick(name[i]);

        }
    }

    private void MyonClick( String s) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mWebView.loadUrl("https://search.bilibili.com/all?keyword="+ item.getTitle() +"&from_source=nav_search_new&order=totalrank&duration=0&tids_1=119");
                return true;
            }
        });
    }
}
