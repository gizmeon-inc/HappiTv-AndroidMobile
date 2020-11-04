package com.happi.android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.webkit.WebView;

import com.happi.android.BuildConfig;
import com.happi.android.R;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.models.ChannelModel;
import com.happi.android.models.IPAddressModel;
import com.happi.android.models.ASTVHome;
import com.happi.android.models.SelectedVideoModel;
import com.happi.android.models.VideoModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FormatAdUrl {

    public static String formatAdUrl(SelectedVideoModel videoModel, IPAddressModel ipAddressModel) {
        String url = videoModel.getAd_link().trim();
        //int videoId = videoModel.getVideo_id();
        String videoId = videoModel.getVideo_id();
        int channelId = Integer.parseInt(videoModel.getChannel_id());
        String duration = videoModel.getVideo_duration();
        String versionName = BuildConfig.VERSION_NAME;
        String bundle_id = HappiApplication.getCurrentContext().getPackageName();
        String device_make = Build.MANUFACTURER;
        String device_model = Build.MODEL;
        String os_version = Build.VERSION.RELEASE;
        String device_type = "Android";
        String device_id = SharedPreferenceUtility.getAdvertisingId();

        int height = 480;
        int width = 640;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (HappiApplication.getCurrentActivity() != null) {
            HappiApplication.getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics
                    (displayMetrics);
            height = displayMetrics.heightPixels;
            width = displayMetrics.widthPixels;
        }
        String advertisingId_fromThread = SharedPreferenceUtility.getAdvertisingId();

        if(HappiApplication.getIpAddress() != null && !HappiApplication.getIpAddress().isEmpty()){
            url = url.replace("[IP_ADDRESS]", HappiApplication.getIpAddress());
        }else{
            if (ipAddressModel != null && ipAddressModel.getQuery() != null) {
                url = url.replace("[IP_ADDRESS]", ipAddressModel.getQuery());
            }
        }

        url = url.replace("[COUNTRY]", SharedPreferenceUtility.getCountry());
        url = url.replace("[CITY]", HappiApplication.getCity());
        url = url.replace("[LATITUDE]", "" + HappiApplication.getLatitude());
        url = url.replace("[LONGITUDE]", "" + HappiApplication.getLongitude());
        url = url.replace("[REGION]", "" + HappiApplication.getRegion());

        if (HappiApplication.getLatitude() != 0 && HappiApplication.getLongitude() != 0) {

            url = url.replace("[LOCSOURCE]", "" + "1");
        } else {

            url = url.replace("[LOCSOURCE]", "2");
        }
        if (advertisingId_fromThread != null) {

            url = url.replace("[DEVICE_IFA]", advertisingId_fromThread);
        }
        String user_agent = new WebView(HappiApplication.getCurrentContext()).getSettings().getUserAgentString();
        if (user_agent != null) {

            url = url.replace("[USER_AGENT]", encodeValue(user_agent));
        } else {

            String ua = "Mozilla/5.0 (Linux; Android 5.1.1; NEO-U1 Build/LMY47V; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/58.0.3029.83 Safari/537.36";
            url = url.replace("[USER_AGENT]", encodeValue(ua));
        }

        url = url.replace("[GDPR]", "0");
        url = url.replace("[AUTOPLAY]", "1");
        url = url.replace("[IAB_CATEGORY]", "IAB1-7");
        url = url.replace("[DESCRIPTION]", "");

        url = url.replace("[HEIGHT]", "" + height);
        url = url.replace("[WIDTH]", "" + width);
        url = url.replace("[DEVICE_ID]", device_id);
        url = url.replace("[DEVICE_TYPE]", device_type);
        url = url.replace("[DEVICE_MAKE]", encodeValue(device_make));
        url = url.replace("[DEVICE_MODEL]", encodeValue(device_model));
        url = url.replace("[APP_VERSION]", versionName);
        url = url.replace("[OS_VER]", os_version);
        url = url.replace("[APP_STORE_URL]", HappiApplication.getCurrentContext().getString(R.string
                .app_store_url));
        url = url.replace("[BUNDLE]", bundle_id);
        url = url.replace("[APPNAME]", encodeValue(HappiApplication.getCurrentContext().getString(R.string
                .app_name)));
        url = url.replace("[APP_NAME]", encodeValue(HappiApplication.getCurrentContext().getString(R.string.app_name)));
        url = url.replace("[VIDEO_ID]", "" + videoId);
        url = url.replace("[CHANNEL_ID]", "" + channelId);
        url = url.replace("[TOTAL_DURATION]", duration);
        url = url.replace("[DURATION]", duration);
        url = url.replace("[DEVICE_ORIGIN]", "AA");
        url = url.replace("[NETWORK]", encodeValue(getNetworkCarrier(1)));
        url = url.replace("[CARRIER]", encodeValue(getNetworkCarrier(2)));
        url = url.replace("[CACHEBUSTER]", String.valueOf(System.currentTimeMillis()));
        url = url.replace("[TYPE]", tabOrPhone());
        url = url.replace("[DNT]", "" + 0);
        url = url.replace("[VPAID]", "" + 0);
        url = url.replace("[PL]", "" + 0);

        if (advertisingId_fromThread != null) {
            url = url.replace("[USER_ID]", String.valueOf(SharedPreferenceUtility.getUserId()));
            url = url.replace("[UUID]", advertisingId_fromThread);
        } else {
            url = url.replace("[USER_ID]", String.valueOf(SharedPreferenceUtility.getUserId()));
            url = url.replace("[UUID]", device_id);
        }

        String category = "";
        if (videoModel.getCategory_name().size() != 0) {
            category = videoModel.getCategory_name().get(0);
        }
        if (videoModel.getCategory_name().size() > 0) {
            for (int i = 1; i < videoModel.getCategory_name().size(); i++) {
                category = category + "," + videoModel.getCategory_name().get(i);
            }
        }
        url = url.replace("[KEYWORDS]", encodeValue(category));

        return url;
    }

    public static String formatChannelAdUrl(ASTVHome pHome, IPAddressModel ipAddressModel) {

        String url = pHome.getAdLink();
        int videoId = pHome.getChannelId();
        int channelId = pHome.getChannelId();
        String duration = "0";
        String versionName = BuildConfig.VERSION_NAME;
        String bundle_id = HappiApplication.getCurrentContext().getPackageName();
        String device_make = Build.MANUFACTURER;
        String device_model = Build.MODEL;
        String os_version = Build.VERSION.RELEASE;
        String device_type = "Android";
        String device_id = SharedPreferenceUtility.getAdvertisingId();

        int height = 480;
        int width = 640;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (HappiApplication.getCurrentActivity() != null) {
            HappiApplication.getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics
                    (displayMetrics);
            height = displayMetrics.heightPixels;
            width = displayMetrics.widthPixels;
        }
        String advertisingId_fromThread = SharedPreferenceUtility.getAdvertisingId();

        if(HappiApplication.getIpAddress() != null && !HappiApplication.getIpAddress().isEmpty()){
            url = url.replace("[IP_ADDRESS]", HappiApplication.getIpAddress());
        }else{
            if (ipAddressModel != null && ipAddressModel.getQuery() != null) {
                url = url.replace("[IP_ADDRESS]", ipAddressModel.getQuery());
            }
        }

        url = url.replace("[COUNTRY]", SharedPreferenceUtility.getCountry());
        url = url.replace("[CITY]", HappiApplication.getCity());
        url = url.replace("[LATITUDE]", "" + HappiApplication.getLatitude());
        url = url.replace("[LONGITUDE]", "" + HappiApplication.getLongitude());
        url = url.replace("[REGION]", "" + HappiApplication.getRegion());

        if (HappiApplication.getLatitude() != 0 && HappiApplication.getLongitude() != 0) {

            url = url.replace("[LOCSOURCE]", "" + "1");
        } else {

            url = url.replace("[LOCSOURCE]", "2");
        }
        if (advertisingId_fromThread != null) {

            url = url.replace("[DEVICE_IFA]", advertisingId_fromThread);
        }
        String user_agent = new WebView(HappiApplication.getCurrentContext()).getSettings().getUserAgentString();
        if (user_agent != null) {

            url = url.replace("[USER_AGENT]", encodeValue(user_agent));
        } else {

            String ua = "Mozilla/5.0 (Linux; Android 5.1.1; NEO-U1 Build/LMY47V; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/58.0.3029.83 Safari/537.36";
            url = url.replace("[USER_AGENT]", encodeValue(ua));
        }

        url = url.replace("[GDPR]", "0");
        url = url.replace("[AUTOPLAY]", "1");
        url = url.replace("[IAB_CATEGORY]", "IAB1-7");
        url = url.replace("[DESCRIPTION]", "");

        url = url.replace("[HEIGHT]", "" + height);
        url = url.replace("[WIDTH]", "" + width);
        url = url.replace("[DEVICE_ID]", device_id);
        url = url.replace("[DEVICE_TYPE]", device_type);
        url = url.replace("[DEVICE_MAKE]", encodeValue(device_make));
        url = url.replace("[DEVICE_MODEL]", encodeValue(device_model));
        url = url.replace("[APP_VERSION]", versionName);
        url = url.replace("[OS_VER]", os_version);
        url = url.replace("[APP_STORE_URL]", HappiApplication.getCurrentContext().getString(R.string
                .app_store_url));
        url = url.replace("[BUNDLE]", bundle_id);
        url = url.replace("[APPNAME]", encodeValue(HappiApplication.getCurrentContext().getString(R.string
                .app_name)));
        url = url.replace("[APP_NAME]", encodeValue(HappiApplication.getCurrentContext().getString(R.string
                .app_name)));
        url = url.replace("[VIDEO_ID]", "" + videoId);
        url = url.replace("[CHANNEL_ID]", "" + channelId);
        url = url.replace("[TOTAL_DURATION]", duration);
        url = url.replace("[DURATION]", duration);
        url = url.replace("[DEVICE_ORIGIN]", "AA");
        url = url.replace("[NETWORK]", encodeValue(getNetworkCarrier(1)));
        url = url.replace("[CARRIER]", encodeValue(getNetworkCarrier(2)));
        url = url.replace("[CACHEBUSTER]", String.valueOf(System.currentTimeMillis()));
        url = url.replace("[TYPE]", tabOrPhone());
        url = url.replace("[DNT]", "" + 0);
        url = url.replace("[VPAID]", "" + 0);
        url = url.replace("[PL]", "" + 0);

        if (advertisingId_fromThread != null) {
            url = url.replace("[USER_ID]", String.valueOf(SharedPreferenceUtility.getUserId()));
            url = url.replace("[UUID]", advertisingId_fromThread);
        } else {
            url = url.replace("[USER_ID]", String.valueOf(SharedPreferenceUtility.getUserId()));
            url = url.replace("[UUID]", device_id);
        }

        return url;
    }
    public static String formatChannelAdUrl(ChannelModel pHome, IPAddressModel ipAddressModel) {

        String url = pHome.getAd_link();
        int videoId = pHome.getChannelId();
        int channelId = pHome.getChannelId();
        String duration = "0";
        String versionName = BuildConfig.VERSION_NAME;
        String bundle_id = HappiApplication.getCurrentContext().getPackageName();
        String device_make = Build.MANUFACTURER;
        String device_model = Build.MODEL;
        String os_version = Build.VERSION.RELEASE;
        String device_type = "Android";
        String device_id = SharedPreferenceUtility.getAdvertisingId();

        int height = 480;
        int width = 640;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (HappiApplication.getCurrentActivity() != null) {
            HappiApplication.getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics
                    (displayMetrics);
            height = displayMetrics.heightPixels;
            width = displayMetrics.widthPixels;
        }
        String advertisingId_fromThread = SharedPreferenceUtility.getAdvertisingId();

        if(HappiApplication.getIpAddress() != null && !HappiApplication.getIpAddress().isEmpty()){
            url = url.replace("[IP_ADDRESS]", HappiApplication.getIpAddress());
        }else{
            if (ipAddressModel != null && ipAddressModel.getQuery() != null) {
                url = url.replace("[IP_ADDRESS]", ipAddressModel.getQuery());
            }
        }

        url = url.replace("[COUNTRY]", SharedPreferenceUtility.getCountry());
        url = url.replace("[CITY]", HappiApplication.getCity());
        url = url.replace("[LATITUDE]", "" + HappiApplication.getLatitude());
        url = url.replace("[LONGITUDE]", "" + HappiApplication.getLongitude());
        url = url.replace("[REGION]", "" + HappiApplication.getRegion());

        if (HappiApplication.getLatitude() != 0 && HappiApplication.getLongitude() != 0) {

            url = url.replace("[LOCSOURCE]", "" + "1");
        } else {

            url = url.replace("[LOCSOURCE]", "2");
        }
        if (advertisingId_fromThread != null) {

            url = url.replace("[DEVICE_IFA]", advertisingId_fromThread);
        }
        String user_agent = new WebView(HappiApplication.getCurrentContext()).getSettings().getUserAgentString();
        if (user_agent != null) {

            url = url.replace("[USER_AGENT]", encodeValue(user_agent));
        } else {

            String ua = "Mozilla/5.0 (Linux; Android 5.1.1; NEO-U1 Build/LMY47V; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/58.0.3029.83 Safari/537.36";
            url = url.replace("[USER_AGENT]", encodeValue(ua));
        }

        url = url.replace("[GDPR]", "0");
        url = url.replace("[AUTOPLAY]", "1");
        url = url.replace("[IAB_CATEGORY]", "IAB1-7");
        url = url.replace("[DESCRIPTION]", "");

        url = url.replace("[HEIGHT]", "" + height);
        url = url.replace("[WIDTH]", "" + width);
        url = url.replace("[DEVICE_ID]", device_id);
        url = url.replace("[DEVICE_TYPE]", device_type);
        url = url.replace("[DEVICE_MAKE]", encodeValue(device_make));
        url = url.replace("[DEVICE_MODEL]", encodeValue(device_model));
        url = url.replace("[APP_VERSION]", versionName);
        url = url.replace("[OS_VER]", os_version);
        url = url.replace("[APP_STORE_URL]", HappiApplication.getCurrentContext().getString(R.string
                .app_store_url));
        url = url.replace("[BUNDLE]", bundle_id);
        url = url.replace("[APPNAME]", encodeValue(HappiApplication.getCurrentContext().getString(R.string
                .app_name)));
        url = url.replace("[APP_NAME]", encodeValue(HappiApplication.getCurrentContext().getString(R.string
                .app_name)));
        url = url.replace("[VIDEO_ID]", "" + videoId);
        url = url.replace("[CHANNEL_ID]", "" + channelId);
        url = url.replace("[TOTAL_DURATION]", duration);
        url = url.replace("[DURATION]", duration);
        url = url.replace("[DEVICE_ORIGIN]", "AA");
        url = url.replace("[NETWORK]", encodeValue(getNetworkCarrier(1)));
        url = url.replace("[CARRIER]", encodeValue(getNetworkCarrier(2)));
        url = url.replace("[CACHEBUSTER]", String.valueOf(System.currentTimeMillis()));
        url = url.replace("[TYPE]", tabOrPhone());
        url = url.replace("[DNT]", "" + 0);
        url = url.replace("[VPAID]", "" + 0);
        url = url.replace("[PL]", "" + 0);

        if (advertisingId_fromThread != null) {
            url = url.replace("[USER_ID]", advertisingId_fromThread);
            url = url.replace("[UUID]", advertisingId_fromThread);
        } else {
            url = url.replace("[USER_ID]", device_id);
            url = url.replace("[UUID]", device_id);
        }

        return url;
    }
    private static String getNetworkCarrier(int value) {

        if (value == 2) {

            TelephonyManager manager = (TelephonyManager) HappiApplication.getCurrentContext().getSystemService
                    (Context.TELEPHONY_SERVICE);
            String carrierName = manager.getNetworkOperatorName();
            if (!carrierName.isEmpty()) {

                return carrierName;
            } else {

                return "no%carrier";
            }
        } else {

            String network = "";
            ConnectivityManager cm = (ConnectivityManager) HappiApplication.getCurrentContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    network = "wifi";
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to mobile data
                    network = "carrier";
                }
            } else {
                // not connected to the internet
                network = "no%20network";
            }
            return network;
        }
    }

    private static String tabOrPhone() {

        DisplayMetrics metrics = new DisplayMetrics();
        HappiApplication.getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches >= 6.5) {
            // 6.5inch device or bigger
            return "tablet";
        } else {
            // smaller device
            return "phone";
        }
    }

    private static String encodeValue(String value) {

        String query = "";
        try {

            query = URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            return value;
        }

        return query;
    }
}
