package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gizmeon on 12-09-2017.
 */

public class RegisterResponseModel {


    @SerializedName("success")
    private boolean success;
    @SerializedName("status")
    private int status;
    @SerializedName("message")
    private String message;


    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
