package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class YTResponse implements Serializable {

    @SerializedName("status")
    private String status= "";
    @SerializedName("data")
    private List<YTModel> data;
    @SerializedName("success")
    private String success= "";
    @SerializedName("message")
    private String message= "";

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public List<YTModel> getData() {
        return data;
    }

    public void setData(List<YTModel> data) {
        this.data = data;
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

}
