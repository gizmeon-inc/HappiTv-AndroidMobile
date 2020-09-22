package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AnalyticsResponseModel implements Serializable {

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
