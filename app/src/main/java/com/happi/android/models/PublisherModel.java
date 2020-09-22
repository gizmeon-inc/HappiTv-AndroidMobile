package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

public class PublisherModel {
    @SerializedName("pubid")
    private String pubid;
    @SerializedName("message")
    private String message;

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
