package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LikeUnlikeModel {

    @SerializedName("status")
    private int status;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("liked_flag")
    private int liked_flag;

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

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLiked_flag() {
        return liked_flag;
    }

    public void setLiked_flag(int liked_flag) {
        this.liked_flag = liked_flag;
    }
}
