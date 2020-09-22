package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetWatchListResponseModel implements Serializable {

    @SerializedName("status")
    private int status;
    @SerializedName("data")
    private List<GetWatchListModel> data;
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

    public List<GetWatchListModel> getData() {
        return data;
    }

    public void setData(List<GetWatchListModel> data) {
        this.data = data;
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
