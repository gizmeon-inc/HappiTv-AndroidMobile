package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SessionTokenResponseModel {


    @SerializedName("status")
    private int status;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("pubid")
    @Expose
    private String publisher_id;
    @SerializedName("rewarded_status")
    @Expose
    private String rewarded_status;

    @SerializedName("app_id")
    @Expose
    private String app_id;

    @SerializedName("rewarded_id")
    @Expose
    private String rewarded_id;

    @SerializedName("banner_id")
    @Expose
    private String banner_id;

    @SerializedName("interstitial_id")
    @Expose
    private String interstitial_id;

    @SerializedName("interstitial_status")
    @Expose
    private String interstitial_status;

    @SerializedName("mopub_banner_id")
    @Expose
    private String mopub_banner_id;

    @SerializedName("mopub_rect_banner_id")
    @Expose
    private String mopub_rect_banner_id;

    @SerializedName("mopub_interstitial_id")
    @Expose
    private String mopub_interstitial_id;

    @SerializedName("mopub_interstitial_status")
    @Expose
    private String mopub_interstitial_status;
    @SerializedName("application_id")
    @Expose
    private String application_id;

    public String getApplication_id() {
        return application_id;
    }

    public void setApplication_id(String application_id) {
        this.application_id = application_id;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public String getRewarded_status() {
        return rewarded_status;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getRewarded_id() {
        return rewarded_id;
    }

    public String getBanner_id() {
            return banner_id;
        }

    public String getInterstitial_id() {
            return interstitial_id;
        }

    public String getInterstitial_status() {
        return interstitial_status;
    }

    public String getMopub_banner_id() {
        return mopub_banner_id;
    }
    public String getMopub_interstitial_id() {
        return mopub_interstitial_id;
    }

    public String getMopub_interstitial_status() {
        return mopub_interstitial_status;
    }


    public void setMopub_interstitial_status(String mopub_interstitial_status) {
        this.mopub_interstitial_status = mopub_interstitial_status;
    }

    public void setMopub_interstitial_id(String mopub_interstitial_id) {
        this.mopub_interstitial_id = mopub_interstitial_id;
    }

    public void setMopub_banner_id(String mopub_banner_id) {
        this.mopub_banner_id = mopub_banner_id;
    }
    public String getMopub_rect_banner_id() {
        return mopub_rect_banner_id;
    }

    public void setMopub_rect_banner_id(String mopub_rect_banner_id) {
        this.mopub_rect_banner_id = mopub_rect_banner_id;
    }
    public void setInterstitial_status(String interstitial_status) {
        this.interstitial_status = interstitial_status;
    }

    public void setInterstitial_id(String interstitial_id) {
        this.interstitial_id = interstitial_id;
    }

    public void setBanner_id(String banner_id) {
        this.banner_id = banner_id;
    }

    public void setRewarded_id(String rewarded_id) {
        this.rewarded_id = rewarded_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public void setRewarded_status(String rewarded_status) {
        this.rewarded_status = rewarded_status;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(String publisher_id) {
        this.publisher_id = publisher_id;
    }
}
