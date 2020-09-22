package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VideoModelUpdated implements Serializable {

    /*              "video_id": 1460,
                    "video_name": "https://gizmeon.s.llnwi.net/vod/201910171571314938/playlist.m3u8",
                    "video_title": "A Musical Journey On the Silk Route Chapter 2",
                    "premium_flag": 1,
                    "thumbnail": "1571314938.jpg",
                    "video_description": "Also    */

    @SerializedName("video_id")
    private Integer video_id;
    @SerializedName("video_name")
    private  String video_name;
    @SerializedName("video_title")
    private  String video_title;
    @SerializedName("premium_flag")
    private Integer premium_flag;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("video_description")
    private String video_description;

    public Integer getVideo_id() {
        return video_id;
    }

    public void setVideo_id(Integer video_id) {
        this.video_id = video_id;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public Integer getPremium_flag() {
        return premium_flag;
    }

    public void setPremium_flag(Integer premium_flag) {
        this.premium_flag = premium_flag;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideo_description() {
        return video_description;
    }

    public void setVideo_description(String video_description) {
        this.video_description = video_description;
    }
}

