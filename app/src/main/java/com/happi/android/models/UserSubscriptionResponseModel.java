package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserSubscriptionResponseModel {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private List<UserSubscriptionModel> data;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("forcibleLogout")
    @Expose
    private boolean forcibleLogout;

    public boolean isForcibleLogout() {
        return forcibleLogout;
    }

    public void setForcibleLogout(boolean forcibleLogout) {
        this.forcibleLogout = forcibleLogout;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<UserSubscriptionModel> getData() {
        return data;
    }

    public void setData(List<UserSubscriptionModel> data) {
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
