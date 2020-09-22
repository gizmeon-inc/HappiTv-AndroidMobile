package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LikedFlagResponseModel implements Serializable {
    /*"status": 100,
    "data": [
        {
            "liked_flag": 1
        }
    ],
    "success": true,
    "message": "liked flag of corresponding user.."*/

    @SerializedName("status")
    private int status;
    @SerializedName("data")
    private List<LikeFlagModel> likeFlagModelList;
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;

    public class LikeFlagModel implements Serializable{
        @SerializedName("liked_flag")
        private int liked_flag;

        public int getLiked_flag() {
            return liked_flag;
        }

        public void setLiked_flag(int liked_flag) {
            this.liked_flag = liked_flag;
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<LikeFlagModel> getLikeFlagModelList() {
        return likeFlagModelList;
    }

    public void setLikeFlagModelList(List<LikeFlagModel> likeFlagModelList) {
        this.likeFlagModelList = likeFlagModelList;
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
