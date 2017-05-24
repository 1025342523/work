package com.yifarj.yifadinghuobao.ui.activity.web;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yifarj.yifadinghuobao.R;
import com.yifarj.yifadinghuobao.ui.activity.base.BaseActivity;
import com.yifarj.yifadinghuobao.view.TitleView;

import butterknife.BindView;


/**
 * 快速入门等web页
 */
public class WebActivity extends BaseActivity {

    private static final long ZOOM_CONTROLS_TIMEOUT = ViewConfiguration.getZoomControlsTimeout() + 200L;


    @BindView(R.id.titleView)
    TitleView titleView;

    @BindView(R.id.webView)
    WebView wvActivity;


    @Override
    public int getLayoutId() {
        return R.layout.activity_quick_introduction;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        titleView = (TitleView) findViewById(R.id.titleView);
        wvActivity = (WebView) findViewById(R.id.webView);
        String url = getIntent().getStringExtra("url");
        if (url == null) {
            url = "http://m.yifarj.com/index.php?&a=lists&typeid=29 ";
        }
        titleView.setLeftIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        wvActivity.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                titleView.setTitle(title);
            }
        });
        wvActivity.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && !url.trim().equals("")) {
                    wvActivity.loadUrl(url);
                }
                return true;
            }
        });
        if (!url.trim().equals("")) {
            wvActivity.loadUrl(url);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        wvActivity.onPause();
        wvActivity.pauseTimers();
    }

    @Override
    public void onResume() {
        super.onResume();
        wvActivity.onResume();
        wvActivity.resumeTimers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wvActivity != null) {
            ViewParent vp = wvActivity.getParent();
            if (vp != null) {
                ((ViewGroup) vp).removeView(wvActivity);
            }
            wvActivity.postDelayed(new Runnable() {// 防止Built in zoom control 导致的window
                @Override
                public void run() {
                    if (wvActivity != null) {
                        wvActivity.destroy();
                        wvActivity = null;
                    }
                }
            }, ZOOM_CONTROLS_TIMEOUT);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (wvActivity != null && wvActivity.canGoBack()) {
            wvActivity.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
