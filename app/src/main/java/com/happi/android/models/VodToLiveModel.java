package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VodToLiveModel implements Serializable {

    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("premium_flag")
    private String premium_flag;
    @SerializedName("video_id")
    private Integer video_id;
    @SerializedName("show_id")
    private String show_id;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPremium_flag() {
        return premium_flag;
    }

    public void setPremium_flag(String premium_flag) {
        this.premium_flag = premium_flag;
    }

    public Integer getVideo_id() {
        return video_id;
    }

    public void setVideo_id(Integer video_id) {
        this.video_id = video_id;
    }

    public String getShow_id() {
        return show_id;
    }

    public void setShow_id(String show_id) {
        this.show_id = show_id;
    }
}
