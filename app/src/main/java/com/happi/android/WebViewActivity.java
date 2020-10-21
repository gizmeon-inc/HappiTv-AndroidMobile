package com.happi.android;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.happi.android.common.HappiApplication;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.utils.ConstantUtils;

public class WebViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String policyUrl;
        String termsUrl;
        if (SharedPreferenceUtility.isNightMode()) {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_black));
            termsUrl = ConstantUtils.termsNightUrl;
            policyUrl = ConstantUtils.policyNightUrl;
        } else {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            int flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            this.getWindow().getDecorView().setSystemUiVisibility(flags);
            termsUrl = ConstantUtils.termsDayUrl;
            policyUrl = ConstantUtils.policyDayUrl;
        }
        setContentView(R.layout.activity_web_view);

        HappiApplication.setCurrentContext(this);
        onCreateBottomNavigationView();
        //updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());

        WebView wv_layout = findViewById(R.id.wv_layout);
        ImageView iv_back = findViewById(R.id.iv_back);
        TypefacedTextViewRegular tv_title = findViewById(R.id.tv_title);
        ProgressBar pb_progressbar = findViewById(R.id.pb_progressbar);

        iv_back.setVisibility(View.VISIBLE);
        tv_title.setVisibility(View.VISIBLE);

        //webview
        WebSettings settings = wv_layout.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        settings.setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 9_3 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13E233 Safari/601.1");


        Intent intent = getIntent();
        String web_view_select = intent.getStringExtra("web_view_select");

        if (web_view_select.equals("T")) {              //terms and conditions

            tv_title.setText(R.string.terms_and_conditions);
            wv_layout.loadUrl(termsUrl);
        } else if (web_view_select.equals("P")) {       //privacy policy

            tv_title.setText(R.string.privacy_policy);
            wv_layout.loadUrl(policyUrl);
        }

        wv_layout.setBackgroundColor(ContextCompat.getColor(WebViewActivity.this,
                R.color.dark_black));
        wv_layout.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {

                    //do your task
                    pb_progressbar.setVisibility(View.GONE);
                    wv_layout.setVisibility(View.VISIBLE);
                }
            }
        });

        iv_back.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    protected void onResume() {

        HappiApplication.setCurrentContext(this);
        updateMenuItem(SharedPreferenceUtility.getCurrentBottomMenu());
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0,0);
    }
}
