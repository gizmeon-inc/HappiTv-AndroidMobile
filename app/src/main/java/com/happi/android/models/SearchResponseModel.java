package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SearchResponseModel implements Serializable {

    @SerializedName("success")
    private boolean success;
    @SerializedName("status")
    private int status;
    @SerializedName("video_data")
    private List<VideoModel> video_data = null;
    @SerializedName("channel_data")
    private List<ChannelModel> channel_data = null;

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

    public List<VideoModel> getVideo_data() {
        return video_data;
    }

    public void setVideo_data(List<VideoModel> video_data) {
        this.video_data = video_data;
    }

    public List<ChannelModel> getChannel_data() {
        return channel_data;
    }

    public void setChannel_data(List<ChannelModel> channel_data) {
        this.channel_data = channel_data;
    }
}
