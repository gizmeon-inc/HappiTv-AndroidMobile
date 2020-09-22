package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ScheduleUpdatedResponseModel implements Serializable {

    @SerializedName("status")
    private Integer status;

    @SerializedName("data")
    private List<ScheduleUpdatedModel> data;

    @SerializedName("success")
    private String success;

    @SerializedName("message")
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<ScheduleUpdatedModel> getData() {
        return data;
    }

    public void setData(List<ScheduleUpdatedModel> data) {
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
