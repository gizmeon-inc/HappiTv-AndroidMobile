package com.happi.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.happi.android.common.AdvertisingIdAsyncTask;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.CustomAlertDialog;
import com.happi.android.customviews.LogoutDialogClass;
import com.happi.android.customviews.TypefacedTextViewLight;
import com.happi.android.models.UserSubscriptionModel;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.webservice.ApiClient;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;

public class MainSubscriptionActivity extends BaseActivity implements LogoutDialogClass.onLogoutClickListener, CustomAlertDialog.OnOkClick {

    private RelativeLayout rl_container_for_image;
    private RelativeLayout rl_main_container;
    private RelativeLayout rl_web_view;
    private WebView wv_subscription;
    //logout
    TypefacedTextViewLight tv_logout;
    TypefacedTextViewLight tv_username;
    private CompositeDisposable compositeDisposable;
    private String token;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_black));
        setContentView(R.layout.activity_main_subscription);
        HappiApplication.setCurrentContext(this);
        if(SharedPreferenceUtility.getAdvertisingId().isEmpty()){
            new AdvertisingIdAsyncTask().execute();
        }
        if(HappiApplication.getIpAddress().isEmpty()){
            getNetworkIP();
        }
        progressDialog = new ProgressDialog(MainSubscriptionActivity.this);

        progressDialog.setCancelable(false);

        compositeDisposable = new CompositeDisposable();
        token = "";
        rl_container_for_image = findViewById(R.id.rl_container_for_image);
        rl_main_container = findViewById(R.id.rl_main_container);
        rl_web_view = findViewById(R.id.rl_web_view);
        wv_subscription = findViewById(R.id.wv_subscription);
        tv_logout = findViewById(R.id.tv_logout);
        tv_username = findViewById(R.id.tv_username);
        String name = "Hi  " + SharedPreferenceUtility.getUserName();
        tv_username.setText(name);
        rl_container_for_image.setVisibility(GONE);
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        progressDialog.setMessage("Loading...");
        progressDialog.show();
        if(HappiApplication.getAppToken() != null && !HappiApplication.getAppToken().isEmpty()){
            fetchToken();
        }else{
            getSessionToken();
        }

//        if (AppUtils.isDeviceRooted()) {
//            showAlertDialogAndExitApp("This device is rooted. You can't use this app.");
//        }
//        wv_subscription.setWebViewClient(new WebViewClient());
//        wv_subscription.setPadding(3, 3, 3, 3);
//        wv_subscription.loadUrl("https://www.google.com/");
    }

    public void getSessionToken() {
        String appKey = SharedPreferenceUtility.getAppKey();
        String bundleId = SharedPreferenceUtility.getBundleID();

        ApiClient.TokenService tokenService = ApiClient.token();
        Disposable tokenDisposable =
                tokenService.getSessionToken(SharedPreferenceUtility.getUserId(), appKey, bundleId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sessionTokenResponseModel -> {
                            Log.d("TAG_PREMIUM_VIDEO", "sample home: sessiontoken");

                            HappiApplication.setAppToken(sessionTokenResponseModel.getToken());
                            SharedPreferenceUtility.setApp_Id(sessionTokenResponseModel.getApplication_id());
                            SharedPreferenceUtility.setAdMobPubIds(sessionTokenResponseModel.getBanner_id(), sessionTokenResponseModel.getRewarded_id(), sessionTokenResponseModel.getInterstitial_id(), sessionTokenResponseModel.getApp_id(), sessionTokenResponseModel.getRewarded_status(), sessionTokenResponseModel.getInterstitial_status(), sessionTokenResponseModel.getMopub_interstitial_id(), sessionTokenResponseModel.getMopub_banner_id(), sessionTokenResponseModel.getMopub_interstitial_status(), sessionTokenResponseModel.getMopub_rect_banner_id());
                            fetchToken();

                        }, throwable -> {

                            Log.e("getSessionToken", throwable.getLocalizedMessage());
                        });
        compositeDisposable.add(tokenDisposable);
    }
    public void fetchToken() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", SharedPreferenceUtility.getUserId());
        jsonObject.addProperty("pubid", SharedPreferenceUtility.getPublisher_id());
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable tokenDisposable =
                usersService.getToken(HappiApplication.getAppToken(), jsonObject)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(tokenResponse -> {
                            if(tokenResponse!=null){
                                if(tokenResponse.getStatus()!=null && tokenResponse.getStatus() == 100){
                                    if(tokenResponse.getData()!=null){
                                        token = tokenResponse.getData().trim();
                                        loadWebView();
                                    }
                                }
                            }else{
                                if(progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(this, "Some error occurred. Please try again after sometime.", Toast.LENGTH_SHORT).show();
                            }

                        }, throwable -> {

                            if(progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            Toast.makeText(this, "Some error occurred. Please try again after sometime.", Toast.LENGTH_SHORT).show();
                            Log.e("getSessionToken", throwable.getLocalizedMessage());
                        });
        compositeDisposable.add(tokenDisposable);
    }
    private void signOut() {
        LogoutDialogClass logoutDialogClass =
                new LogoutDialogClass(HappiApplication.getCurrentActivity(), this::onLogoutClicked);
        Objects.requireNonNull(logoutDialogClass.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        logoutDialogClass.show();
    }
    private void loadWebView(){
        rl_container_for_image.setVisibility(GONE);
        rl_main_container.setVisibility(View.VISIBLE);
        String url = ConstantUtils.SUBSCRIPTION_WEBVIEW_URL.trim()+token.trim();
        Log.v("okhttp","MAIN: url: "+url);
        WebSettings settings = wv_subscription.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        settings.setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 9_3 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13E233 Safari/601.1");

        wv_subscription.setWebViewClient(new HappiTVWebViewClient());
        wv_subscription.loadUrl(url);

    }

    @Override
    public void onOkClickNeutral() {
        logoutApiCall();
    }

    private class HappiTVWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.v("okhttp","FINISHED: url: "+url);
            if(url!=null && !url.isEmpty()){
                if(url.contains(ConstantUtils.SUBSCRIPTION_WEBVIEW_URL.trim())){
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if(url != null && !url.trim().isEmpty()){
                if (url.trim().equalsIgnoreCase(ConstantUtils.SUBSCRIPTION_WEBVIEW_URL_SUCCESS.trim())){
                    progressDialog.setMessage("Processing your payment...");
                    progressDialog.show();
                    rl_container_for_image.setVisibility(View.VISIBLE);
                    rl_main_container.setVisibility(GONE);
                    updateSubscriptionIds();

                }else if(url.trim().equalsIgnoreCase(ConstantUtils.SUBSCRIPTION_WEBVIEW_URL_FAILURE.trim())){
                    rl_container_for_image.setVisibility(View.VISIBLE);
                    rl_main_container.setVisibility(GONE);
                    showAlertDialog("Sorry, your Subscription has failed.");

                }
            }
            Log.v("okhttp","onPageStarted: url: "+url);
        }

    }
    private void showAlertDialog(String message) {
        CustomAlertDialog alertDialog =
                new CustomAlertDialog(HappiApplication.getCurrentActivity(), "ok", message, "Ok", "", null, null, this::onOkClickNeutral, null);
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    private void updateSubscriptionIds() {

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable subscriptionDisposable = usersService.getUserSubscriptions(HappiApplication.getAppToken(),
                SharedPreferenceUtility.getUserId(), SharedPreferenceUtility.getAdvertisingId(),
                SharedPreferenceUtility.getCountryCode(), SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriptionResponseModel -> {
                    List<String> subids = new ArrayList<>();
                    if (subscriptionResponseModel.getData().size() != 0) {

                        List<UserSubscriptionModel> subscriptionModelList = subscriptionResponseModel.getData();

                        for (UserSubscriptionModel item : subscriptionModelList) {
                            subids.add(item.getSub_id());
                        }
                    }
                    HappiApplication.setSub_id(subids);
                    if(!HappiApplication.getSub_id().isEmpty()){
                        goToHome();
                    }else{
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        showAlertDialog("Your Subscription is being processed. Please try again after sometime.");

                    }

                }, throwable -> {
                    //hideProgressDialog();
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(this, "Something went wrong. Please try again after sometime.", Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(subscriptionDisposable);
    }

    @Override
    public void onLogoutClicked() {
        logoutApiCall();
    }
    private void logoutApiCall() {
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable logoutDisposable = usersService.logout(SharedPreferenceUtility.getUserId(), SharedPreferenceUtility.getPublisher_id(),
                SharedPreferenceUtility.getAdvertisingId(), HappiApplication.getIpAddress())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logoutResponseModel -> {

                    if (logoutResponseModel.getStatus() == 100) {

                        SharedPreferenceUtility.saveUserDetails(0, "", "", "", "", "", "", "", false, "");
                        SharedPreferenceUtility.setGuest(false);
                        SharedPreferenceUtility.setIsFirstTimeInstall(false);
                        SharedPreferenceUtility.setChannelId(0);
                        SharedPreferenceUtility.setShowId("0");
                        SharedPreferenceUtility.setVideoId(0);
                        SharedPreferenceUtility.setCurrentBottomMenuIndex(0);
                        SharedPreferenceUtility.setChannelTimeZone("");
                        SharedPreferenceUtility.setSession_Id("");
                        SharedPreferenceUtility.setNotificationIds(new ArrayList<>());
                        SharedPreferenceUtility.setSubscriptionItemIdList(new ArrayList<>());

                        HappiApplication.setSub_id(new ArrayList<>());

                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Intent intent = new Intent(MainSubscriptionActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else {
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(this, "Some error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                        //  Log.e("Logout","api call failed");
                    }

                }, throwable -> {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(this, "Some error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                    //  Log.e("Logout","api call failed");
                });

        compositeDisposable.add(logoutDisposable);
    }
    private void getNetworkIP() {
        boolean isMobileData = false;
        boolean isWifi = false;
        String ipAddress = "";

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getTypeName().equalsIgnoreCase("WIFI")) {
                if (networkInfo.isConnected()) {
                    isWifi = true;
                } else {
                    isWifi = false;
                }
            }

            if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (networkInfo.isConnected()) {
                    isMobileData = true;
                } else {
                    isMobileData = false;
                }

            }
        }

        if (isWifi) {
            ipAddress = getWifiIpAddress();
            HappiApplication.setIpAddress(ipAddress);
        }
        if (isMobileData) {
            ipAddress = getMobileIpAddress();
            HappiApplication.setIpAddress(ipAddress);
        }
        //Log.e("1234###","ipAddressFinal: "+ipAddressFinal);
    }

    private String getWifiIpAddress() {
        @SuppressWarnings("deprecation")
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        return ip;
    }

    private String getMobileIpAddress() {
        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumipAddr = networkInterface.getInetAddresses(); enumipAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        // if(inetAddress instanceof Inet4Address)
                        return Formatter.formatIpAddress(inetAddress.hashCode());

                    }

                }
            }

        } catch (Exception ex) {
            // Log.e("1234###", "exception: " + ex.toString());
        }
        return null;
    }

    private void goToHome(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        Intent homeIntent = new Intent(MainSubscriptionActivity.this, MainHomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

}