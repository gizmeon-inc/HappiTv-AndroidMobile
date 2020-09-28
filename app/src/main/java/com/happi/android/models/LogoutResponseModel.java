package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LogoutResponseModel implements Serializable {

    @SerializedName("status")
    private int status;
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

