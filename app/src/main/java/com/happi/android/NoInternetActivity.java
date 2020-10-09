package com.happi.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;

public class NoInternetActivity extends BaseActivity {

    ImageView iv_retry;
    LinearLayout ll_retry;
    Animation rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreferenceUtility.isNightMode()) {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_black));
        } else {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            int flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            this.getWindow().getDecorView().setSystemUiVisibility(flags);
        }
        setContentView(R.layout.activity_no_internet);

        ll_retry = findViewById(R.id.ll_retry);
        iv_retry = findViewById(R.id.iv_retry);
        rotation = AnimationUtils.loadAnimation(NoInternetActivity.this, R.anim.rotate);
        rotation.setFillAfter(true);

        ll_retry.setOnClickListener(v -> {

            iv_retry.startAnimation(rotation);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms

                    if (isNetworkConnected()) {

                        iv_retry.clearAnimation();
                        Toast.makeText(HappiApplication.getCurrentContext(), "We're back", Toast
                                .LENGTH_SHORT).show();
                        NoInternetActivity.this.finish();
                    } else {

                        iv_retry.clearAnimation();
                        Toast.makeText(HappiApplication.getCurrentContext(), "No internet " +
                                "connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 2000);
        });
    }

    public boolean isNetworkConnected() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
       // NetworkInfo netInfo = cm.getActiveNetworkInfo();
        NetworkInfo netInfo = null;
        if(cm != null && cm.getActiveNetworkInfo() != null){
            netInfo = cm.getActiveNetworkInfo();
        }
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        HappiApplication.setCurrentContext(this);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(networkReceiver);
        super.onPause();
    }

    /*private BroadcastReceiver networkReceiver123 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getExtras() != null) {

                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    // we're connected
                    NoInternetActivity.this.finish();
                }
            }
        }
    };*/


    public BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                //NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                ConnectivityManager connectivityManager = (ConnectivityManager) HappiApplication.getCurrentContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = null;
                if(connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null){
                    networkInfo = connectivityManager.getActiveNetworkInfo();
                }else{
                    networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                }
                if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    NoInternetActivity.this.finish();
                } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {

                }
            }
        }
    };

    @Override
    public void onBackPressed() {

    }
}
