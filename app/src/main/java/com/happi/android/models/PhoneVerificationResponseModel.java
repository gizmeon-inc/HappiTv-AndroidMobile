package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhoneVerificationResponseModel {
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("data")
    @Expose
    private List<PhoneVerificationModel> data;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<PhoneVerificationModel> getData() {
        return data;
    }

    public void setData(List<PhoneVerificationModel> data) {
        this.data = data;
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
}
