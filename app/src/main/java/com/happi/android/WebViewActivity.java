package com.happi.android;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.webkit.WebChromeClient;
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

        WebView wv_layout = findViewById(R.id.wv_layout);
        ImageView iv_menu = findViewById(R.id.iv_menu);
        ImageView iv_back = findViewById(R.id.iv_back);
        ImageView iv_logo_text = findViewById(R.id.iv_logo_text);
        TypefacedTextViewRegular tv_title = findViewById(R.id.tv_title);
        ImageView iv_search = findViewById(R.id.iv_search);
        ProgressBar pb_progressbar = findViewById(R.id.pb_progressbar);

        iv_menu.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        iv_logo_text.setVisibility(View.GONE);
        tv_title.setVisibility(View.VISIBLE);
        iv_search.setVisibility(View.GONE);

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

            super.onBackPressed();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        HappiApplication.setCurrentContext(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0,0);
    }
}
