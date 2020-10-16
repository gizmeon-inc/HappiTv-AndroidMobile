package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PartnerShowsModel implements Serializable {
    @SerializedName("show_id")
    @Expose
    private String show_id;
    @SerializedName("show_name")
    @Expose
    private String show_name;

    @SerializedName("single_video")
    private int single_video;

    @SerializedName("videos")
    @Expose
    private List<VideoModelUpdated> videoModelUpdatedList;

    public int getSingle_video() {
        return single_video;
    }

    public void setSingle_video(int single_video) {
        this.single_video = single_video;
    }

    public String getShow_id() {
        return show_id;
    }

    public String getShow_name() {
        return show_name;
    }

    public List<VideoModelUpdated> getVideoModelUpdatedList() {
        return videoModelUpdatedList;
    }
}
