package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

public class PublisherModel {
    @SerializedName("pubid")
    private String pubid;
    @SerializedName("message")
    private String message;
    @SerializedName("registration_mandatory_flag")
    private boolean registration_mandatory_flag;
    @SerializedName("subscription_mandatory_flag")
    private boolean subscription_mandatory_flag;

    public boolean isRegistration_mandatory_flag() {
        return registration_mandatory_flag;
    }

    public void setRegistration_mandatory_flag(boolean registration_mandatory_flag) {
        this.registration_mandatory_flag = registration_mandatory_flag;
    }

    public boolean isSubscription_mandatory_flag() {
        return subscription_mandatory_flag;
    }

    public void setSubscription_mandatory_flag(boolean subscription_mandatory_flag) {
        this.subscription_mandatory_flag = subscription_mandatory_flag;
    }

    public String getPubid() {
        return pubid;
    }

    public void setPubid(String pubid) {
        this.pubid = pubid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
