package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ShowListResponseModel implements Serializable {


    @SerializedName("status")
    private String status;
    @SerializedName("success")
    private String success;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<ShowModel> showModelList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ShowModel> getShowModelList() {
        return showModelList;
    }

    public void setShowModelList(List<ShowModel> showModelList) {
        this.showModelList = showModelList;
    }
}
