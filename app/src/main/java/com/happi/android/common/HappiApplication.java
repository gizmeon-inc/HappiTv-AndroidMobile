package com.happi.android.common;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import com.happi.android.models.FeaturedShowsModel;
import com.happi.android.models.ShowModel;
import com.happi.android.webservice.AnalyticsApi;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HappiApplication extends MultiDexApplication implements AppLifecycleHandler.LifeCycleDelegate {
    private static final String TAG = HappiApplication.class.getSimpleName();
    private static Context mContext;
    private static Activity mActivity;
    private static double latitude = 0;
    private static double longitude = 0;
    private static String city = "";
    private static String region = "";
    public static String appToken = "";
    public static List<String> sub_id = new ArrayList<>();
    public static int updateTransactionCount = 3;

    //------------------------new changes------------------------------------//
    public static ShowModel showModel = new ShowModel();
    public static FeaturedShowsModel featureShowModel = new FeaturedShowsModel();
    public static boolean isFeaturedShow = false;
    public static String redirect = "";
    public static String categoryId = "";
    public static String showId = "";
    public static boolean isNewSubscriber = false;
    public static boolean isNewLoginFromPremiumPage = false;
    public static boolean isFromLink = false;
    public static boolean needsToBeRefreshed = false;

    //analytics
    public static String ipAddress = "";
    public static boolean applaunch = false;
    public AppLifecycleHandler appLifecycleHandlerthis;
    public static String country = "";

    public static boolean isDatePickerClicked = false;


    //getter and setter
    private static boolean hasUpdatedReceipt;

    public static boolean getHasUpdatedReceipt() {
        return hasUpdatedReceipt;
    }

    public static void setHasUpdatedReceipt(boolean hasUpdatedReceipt) {
        HappiApplication.hasUpdatedReceipt = hasUpdatedReceipt;
    }


    public static void setIsDatePickerClicked(boolean isDatePickerClicked) {
        HappiApplication.isDatePickerClicked = isDatePickerClicked;
    }

    public static String getCountry() {
        return country;
    }

    public static void setCountry(String country) {
        HappiApplication.country = country;
    }


    public static boolean isNeedsToBeRefreshed() {
        return needsToBeRefreshed;
    }

    public static void setNeedsToBeRefreshed(boolean needsToBeRefreshed) {
        HappiApplication.needsToBeRefreshed = needsToBeRefreshed;
    }

    public static boolean isApplaunch() {
        return applaunch;
    }

    public static void setApplaunch(boolean applaunch) {
        HappiApplication.applaunch = applaunch;
    }

    public static String getIpAddress() {
        return ipAddress;
    }

    public static void setIpAddress(String ipAddress) {
        HappiApplication.ipAddress = ipAddress;
    }

    public static boolean isIsFromLink() {
        return isFromLink;
    }

    public static void setIsFromLink(boolean isFromLink) {
        HappiApplication.isFromLink = isFromLink;
    }

    public static boolean isIsNewLoginFromPremiumPage() {
        return isNewLoginFromPremiumPage;
    }

    public static void setIsNewLoginFromPremiumPage(boolean isNewLoginFromPremiumPage) {
        HappiApplication.isNewLoginFromPremiumPage = isNewLoginFromPremiumPage;
    }

    public static boolean isIsNewSubscriber() {
        return isNewSubscriber;
    }

    public static void setIsNewSubscriber(boolean isNewSubscriber) {
        HappiApplication.isNewSubscriber = isNewSubscriber;
    }

    public static String getShowId() {
        return showId;
    }

    public static void setShowId(String showId) {
        HappiApplication.showId = showId;
    }

    public static String getCategoryId() {
        return categoryId;
    }

    public static void setCategoryId(String categoryId) {
        HappiApplication.categoryId = categoryId;
    }

    public static String getRedirect() {
        return redirect;
    }

    public static void setRedirect(String redirect) {
        HappiApplication.redirect = redirect;
    }

    public static boolean isIsFeaturedShow() {
        return isFeaturedShow;
    }

    public static void setIsFeaturedShow(boolean isFeaturedShow) {
        HappiApplication.isFeaturedShow = isFeaturedShow;
    }

    public static ShowModel getShowModel() {
        return showModel;
    }

    public static void setShowModel(ShowModel showModel) {
        HappiApplication.showModel = showModel;
    }

    public static FeaturedShowsModel getFeatureShowModel() {
        return featureShowModel;
    }

    public static void setFeatureShowModel(FeaturedShowsModel featureShowModel) {
        HappiApplication.featureShowModel = featureShowModel;
    }

    //--------------------------------------------------------------------//


    public static int getUpdateTransactionCount() {
        return updateTransactionCount;
    }

    public static void setUpdateTransactionCount(int updateTransactionCount) {
        HappiApplication.updateTransactionCount = updateTransactionCount;
    }

    public static List<String> getSub_id() {
        return sub_id;
    }

    public static void setSub_id(List<String> sub_id) {
        HappiApplication.sub_id = sub_id;
    }

    //         50006  5007
    /*public static String getPublisher_id() {
        return publisher_id;
    }

    public static void setPublisher_id(String publisher_id) {
        FEApplication.publisher_id = publisher_id;
    }*/

    public static Context getCurrentContext() {
        return mContext;
    }

    public static void setCurrentContext(Context mContext) {

        HappiApplication.mContext = mContext;
        HappiApplication.mActivity = (Activity) mContext;
    }

    public static Activity getCurrentActivity() {
        return mActivity;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        HappiApplication.latitude = latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longitude) {
        HappiApplication.longitude = longitude;
    }

    public static String getCity() {
        return city;
    }

    public static void setCity(String city) {
        HappiApplication.city = city;
    }

 /*   public static String getCountryCode() {
        return countryCode;
    }

    public static void setCountryCode(String countryCode) {
        FEApplication.countryCode = countryCode;
    }*/

    public static String getRegion() {
        return region;
    }

    public static void setRegion(String region) {
        HappiApplication.region = region;
    }

    public static String getAppToken() {
        return appToken;
    }

    public static void setAppToken(String appToken) {
        HappiApplication.appToken = appToken;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        appLifecycleHandlerthis = new AppLifecycleHandler(this::onAppBackgrounded);
        registerLifecycleHandler(appLifecycleHandlerthis);

        //Test APPID
        //MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        //Live APPID
        MobileAds.initialize(this, "ca-app-pub-1674238972502360~6501892347");


        SharedPreferenceUtility.setNightMode(true);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

    }

    private void registerLifecycleHandler(AppLifecycleHandler appLifecycleHandler) {
        registerActivityLifecycleCallbacks(appLifecycleHandler);
        registerComponentCallbacks(appLifecycleHandler);
    }

    @Override
    public void onAppBackgrounded() {
        // Log.e("ZXCZXC","APP BACKGROUNDED");
        callAnalyticsApiForAppInBackground();
    }

    private void callAnalyticsApiForAppInBackground() {
        //Uncomment to enable analytics api call

        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long epoch = currentCalendar.getTimeInMillis() / 1000L;

        String device_id = SharedPreferenceUtility.getAdvertisingId();

        JsonObject details = new JsonObject();
        details.addProperty("device_id", device_id);
        details.addProperty("user_id", String.valueOf(SharedPreferenceUtility.getUserId()));
        details.addProperty("event_type", "POP06");
        details.addProperty("timestamp", String.valueOf(epoch));
        details.addProperty("app_id", SharedPreferenceUtility.getApp_Id());
        details.addProperty("session_id", SharedPreferenceUtility.getSession_Id());
        details.addProperty("publisherid", SharedPreferenceUtility.getPublisher_id());

        try {
            //  Log.e("000##", " POP06: " + "api about to call");
            AnalyticsApi.AnalyticsServiceScalar analyticsServiceScalar = AnalyticsApi.createScalar();
            Call<String> calls = analyticsServiceScalar.eventCall2(details);
            calls.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    //   Log.e("000##", "success: " + "POP06" + " - " + response.code());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    //  Log.e("000##", "failure: " + "POP06" + " - " + t.toString());
                }
            });
        } catch (Exception ex) {
            // Log.e("000##", "exception: " + "POP06" + " - " + ex.toString());
        }
    }
}
