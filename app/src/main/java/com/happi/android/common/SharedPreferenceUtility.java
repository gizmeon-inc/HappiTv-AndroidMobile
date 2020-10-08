package com.happi.android.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.happi.android.R;

import java.util.ArrayList;


public class SharedPreferenceUtility {


    private static SharedPreferences sharedPreferences;


    public static SharedPreferences getSharedPreferenceInstance() {
        if (sharedPreferences == null)
            sharedPreferences = HappiApplication.getCurrentContext().getSharedPreferences(
                    HappiApplication.getCurrentContext().getString(R.string.USER_PREFERENCES),
                    Context.MODE_PRIVATE);

        return sharedPreferences;
    }



    public static boolean isNightMode() {
        return getSharedPreferenceInstance().getBoolean("appTheme", false);
    }

    public static void setNightMode(boolean appTheme) {

        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putBoolean("appTheme", appTheme);
        sharedPreferencesEditor.commit();
    }

    public static String getFcmToken() {
        return getSharedPreferenceInstance().getString("fcmToken", "");
    }

    public static void setFcmToken(String fcmToken) {

        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putString("fcmToken", fcmToken);
        sharedPreferencesEditor.commit();
    }

    public static void setGuest(boolean isGuest) {

        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putBoolean("isGuest", isGuest);
        sharedPreferencesEditor.commit();

    }

    public static boolean getGuest() {
        return getSharedPreferenceInstance().getBoolean("isGuest", false);
    }

    public static void setVideoId(int videoId) {

        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putInt("videoId", videoId);
        sharedPreferencesEditor.commit();

    }

    public static int getVideoId() {
        return getSharedPreferenceInstance().getInt("videoId", 0);
    }

    public static void setChannelId(int channelId) {

        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putInt("channelId", channelId);
        sharedPreferencesEditor.commit();

    }

    public static int getChannelId() {
        return getSharedPreferenceInstance().getInt("channelId", 0);
    }

    /////////////////////////////////////////
   /* public static int getCommomId() {
        return getSharedPreferenceInstance().getInt("commonId", 0);

    }
    public static void setCommomId(int commonId){
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putInt("commonId", commonId);
        sharedPreferencesEditor.commit();
    }*/
    ////////////////////////////////////////////

    public static void saveUserDetails(int userId, String userName, String userEmail,
                                       String userPassword, String userLoginType, String userDeviceId,
                                       String userDeviceType, String userFbId, boolean guestStatus, String phone) {

        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putInt("userId", userId);
        sharedPreferencesEditor.putString("userName", userName);
        sharedPreferencesEditor.putString("userEmail", userEmail);
        sharedPreferencesEditor.putString("userPassword", userPassword);
        sharedPreferencesEditor.putString("userLoginType", userLoginType);
        sharedPreferencesEditor.putString("userDeviceId", userDeviceId);
        sharedPreferencesEditor.putString("userDeviceType", userDeviceType);
        sharedPreferencesEditor.putString("userFbId", userFbId);
        sharedPreferencesEditor.putString("userPhone", phone);
        sharedPreferencesEditor.putBoolean("guestStatus", guestStatus);
        sharedPreferencesEditor.commit();
    }

    public static int getUserId() {
        return getSharedPreferenceInstance().getInt("userId", 0);
    }

    public static String getUserName() {
        return getSharedPreferenceInstance().getString("userName", "");
    }

    public static String getUserEmail() {
        return getSharedPreferenceInstance().getString("userEmail", "");
    }


    public static String getUserContact() {
        return getSharedPreferenceInstance().getString("userPhone", "");
    }

    public static void setLocationAcceptance(int locationAccepted) {

        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putInt("locationAccepted", locationAccepted);
        sharedPreferencesEditor.commit();
    }

    public static int isLocationAccepted() {
        return getSharedPreferenceInstance().getInt("locationAccepted", 0);
    }
    // 0 - Not requested;
    // 1 - Accepted;
    // 2 - Rejected


    public static void setAdMobPubIds(String bannerAdPubId, String rewardedVideoPubId, String interstitialAdPubId, String appId, String rewardedStatus, String interstitialStatus, String mopub_interstitial_id, String mopub_banner_id, String mopub_interstitial_status, String mopub_rect_banner_id) {

        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putString("bannerAdPubId", bannerAdPubId);
        sharedPreferencesEditor.putString("rewardedVideoPubId", rewardedVideoPubId);
        sharedPreferencesEditor.putString("interstitialAdPubId", interstitialAdPubId);
        sharedPreferencesEditor.putString("appId", appId);
        sharedPreferencesEditor.putString("rewardedStatus", rewardedStatus);
        sharedPreferencesEditor.putString("interstitialStatus", interstitialStatus);
        sharedPreferencesEditor.putString("mopub_interstitial_status", mopub_interstitial_status);
        sharedPreferencesEditor.putString("mopub_banner_id", mopub_banner_id);
        sharedPreferencesEditor.putString("mopub_interstitial_id", mopub_interstitial_id);
        sharedPreferencesEditor.putString("mopub_rect_banner_id", mopub_rect_banner_id);

        sharedPreferencesEditor.commit();
    }


    public static String getAdMobBannerAdPubId() {
        return getSharedPreferenceInstance().getString("bannerAdPubId", "");
    }

    public static String getAdMobRewardedVideoPubId() {
        return getSharedPreferenceInstance().getString("rewardedVideoPubId", "");
    }

    public static String getAdMobInterstitialAdPubId() {
        return getSharedPreferenceInstance().getString("interstitialAdPubId", "");
    }

    public static String getappId() {
        return getSharedPreferenceInstance().getString("appId", "");
    }

    public static String getrewardedStatus() {
        //return getSharedPreferenceInstance().getString("rewardedStatus", "");
        return getSharedPreferenceInstance().getString("noAd", "0");
    }

    public static String getinterstitialStatus() {
        // return getSharedPreferenceInstance().getString("interstitialStatus", "");
        return getSharedPreferenceInstance().getString("noAd", "0");
    }

    public static String getmopub_interstitial_id() {
        // return getSharedPreferenceInstance().getString("mopub_interstitial_id", "");
        return getSharedPreferenceInstance().getString("noAd", "0");
    }

    public static String getmopub_banner_id() {
        return getSharedPreferenceInstance().getString("mopub_banner_id", "");
    }

    public static String getmopub_rect_banner_id() {
        return getSharedPreferenceInstance().getString("mopub_rect_banner_id", "");
    }

    public static String getmopub_interstitial_status() {
        // return getSharedPreferenceInstance().getString("mopub_interstitial_status", "");
        return getSharedPreferenceInstance().getString("noAd", "0");
    }

    public static void setBundleId(String bundleId) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putString("bundleId", bundleId);
        sharedPreferencesEditor.commit();
    }

    public static String getBundleID() {
        return getSharedPreferenceInstance().getString("bundleId", "empty");
    }

    public static void setAppkey(String appKey) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putString("appKey", appKey);
        sharedPreferencesEditor.commit();

    }

    public static String getAppKey() {
        return getSharedPreferenceInstance().getString("appKey", "empty");
    }


    public static void setCountryCode(String countryCode) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putString("countryCode", countryCode);
        sharedPreferencesEditor.commit();

    }

    public static String getCountryCode() {
        return getSharedPreferenceInstance().getString("countryCode", "empty");
    }

    public static String getPublisher_id() {
        return getSharedPreferenceInstance().getString("pubid", "empty");
    }

    public static void setPublisher_id(String publisher_id) {
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putString("pubid", publisher_id);
        sharedPreferencesEditor.commit();
    }

    //analytics
    public static void setIsFirstTimeInstall(boolean isFirstTime) {
        SharedPreferences.Editor sharedPrefEditor = getSharedPreferenceInstance().edit();
        sharedPrefEditor.putBoolean("isFirstTimeInstall", isFirstTime);
        sharedPrefEditor.commit();
    }

    public static boolean getIsFirstTimeInstall() {
        return getSharedPreferenceInstance().getBoolean("isFirstTimeInstall", false);
    }

    public static void setSession_Id(String session_id) {
        SharedPreferences.Editor editor = getSharedPreferenceInstance().edit();
        editor.putString("session_id", session_id);
        editor.commit();
    }

    public static String getSession_Id() {
        return getSharedPreferenceInstance().getString("session_id", "");
    }

    public static void setChannelTimeZone(String timezone) {
        SharedPreferences.Editor editor = getSharedPreferenceInstance().edit();
        editor.putString("channel_timeZone", timezone);
        editor.commit();
    }

    public static String getChannelTimeZone() {
        return getSharedPreferenceInstance().getString("channel_timeZone", "");
    }

    public static void setApp_Id(String app_id) {
        SharedPreferences.Editor editor = getSharedPreferenceInstance().edit();
        editor.putString("app_id", app_id);
        editor.commit();
    }

    public static String getApp_Id() {
        return getSharedPreferenceInstance().getString("app_id", "");
    }

    public static void setNotificationIds(ArrayList<Integer> idList) {
        ArrayList<Integer> idListLocal = idList;
        StringBuilder str = new StringBuilder();
        if (idListLocal.size() > 0) {
            for (int i = 0; i < idListLocal.size(); i++) {
                str.append(idListLocal.get(i)).append(",");
            }
        } else {
            idListLocal = new ArrayList<>();
        }
        SharedPreferences.Editor editor = getSharedPreferenceInstance().edit();
        editor.putString("notification_id", str.toString());
        editor.commit();
    }

    public static ArrayList<Integer> getNotificationIds() {
        String ids = getSharedPreferenceInstance().getString("notification_id", "");
        ArrayList<Integer> listOfNotificationIds = new ArrayList<>();
        String[] listInString = ids.split(",");
        if (listInString.length > 0) {
            for (int ind = 0; ind < listInString.length; ind++) {
                if (!listInString[ind].isEmpty()) {
                    int id = Integer.parseInt(listInString[ind]);
                    listOfNotificationIds.add(id);
                }
            }
        }
        return listOfNotificationIds;
    }

    public static void setIsLiveVisible(boolean isLiveVisible) {
        SharedPreferences.Editor editor = getSharedPreferenceInstance().edit();
        editor.putBoolean("isLiveVisible", isLiveVisible);
        editor.commit();
    }

    public static boolean getIsLiveVisible() {
        return getSharedPreferenceInstance().getBoolean("isLiveVisible", false);
    }

    public static String getShowId() {
        return getSharedPreferenceInstance().getString("showId", "");
    }

    public static void setShowId(String showId) {
        SharedPreferences.Editor editor = getSharedPreferenceInstance().edit();
        editor.putString("showId", showId);
        editor.commit();
    }

    public static String getAdvertisingId() {
        return getSharedPreferenceInstance().getString("AdvertisingId", "");
    }

    public static void setAdvertisingId(String advertisingId) {
        SharedPreferences.Editor editor = getSharedPreferenceInstance().edit();
        editor.putString("AdvertisingId", advertisingId);
        editor.commit();
    }

    public static ArrayList<String> getSubscriptionItemIdList() {
        String ids = getSharedPreferenceInstance().getString("SubscriptionItemIdList", "");
        ArrayList<String> listOfSubscriptionIds = new ArrayList<>();
        String[] listInString = ids.split(",");
        if (listInString.length > 0) {
            for (String s : listInString) {
                if (!s.isEmpty()) {
                    listOfSubscriptionIds.add(s);
                }
            }
        }
        return listOfSubscriptionIds;
    }

    public static void setSubscriptionItemIdList(ArrayList<String> idList) {
        ArrayList<String> idListLocal = idList;
        StringBuilder str = new StringBuilder();
        if (idListLocal.size() > 0) {
            for (int i = 0; i < idListLocal.size(); i++) {
                str.append(idListLocal.get(i)).append(",");
            }
        } else {
            idListLocal = new ArrayList<>();
        }
        SharedPreferences.Editor editor = getSharedPreferenceInstance().edit();
        editor.putString("SubscriptionItemIdList", str.toString());
        editor.commit();
    }


    public static void setCurrentBottomMenuIndex(int menu_id) {
        SharedPreferences.Editor editor = getSharedPreferenceInstance().edit();
        editor.putInt("menu_id", menu_id);
        editor.commit();
    }

    public static int getCurrentBottomMenu() {
        return getSharedPreferenceInstance().getInt("menu_id", 0);
    }

    public static boolean isRegistration_mandatory_flag() {
        return getSharedPreferenceInstance().getBoolean("registration_mandatory_flag", false);
    }

    public static void setRegistration_mandatory_flag(boolean isRegistrationMandatory) {

        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putBoolean("registration_mandatory_flag", isRegistrationMandatory);
        sharedPreferencesEditor.commit();
    }
    public static boolean isSubscription_mandatory_flag() {
        return getSharedPreferenceInstance().getBoolean("subscription_mandatory_flag", false);
    }

    public static void setSubscription_mandatory_flag(boolean isSubscriptionMandatory) {

        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putBoolean("subscription_mandatory_flag", isSubscriptionMandatory);
        sharedPreferencesEditor.commit();
    }

    public static void setPartnerId(String partnerId) {

        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferenceInstance().edit();
        sharedPreferencesEditor.putString("partnerId", partnerId);
        sharedPreferencesEditor.commit();

    }

    public static String getPartnerId() {
        return getSharedPreferenceInstance().getString("partnerId", "0");
    }

}
