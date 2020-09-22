package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gizmeon on 20-02-2018.
 */

public class VideoResponse implements Serializable {

    @SerializedName("success")
    private boolean success;
    @SerializedName("status")
    private int status;
    @SerializedName("data")
    private List<VideoModel> data;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<VideoModel> getData() {
        return data;
    }

    public void setData(List<VideoModel> data) {
        this.data = data;
    }
}
