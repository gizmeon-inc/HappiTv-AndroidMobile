package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetPayperviewVideoListModel implements Serializable {

    @SerializedName("publisher_subscription_id")
    private int publisher_subscription_id;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("video_id")
    private int video_id;
    @SerializedName("valid_from")
    private String valid_from;
    @SerializedName("valid_to")
    private String valid_to;
    @SerializedName("video_title")
    private String video_title;

    private boolean isSelected = false;


    public int getPublisher_subscription_id() {
        return publisher_subscription_id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getVideo_id() {
        return video_id;
    }

    public String getValid_from() {
        return valid_from;
    }

    public String getValid_to() {
        return valid_to;
    }

    public String getVideo_title() {
        return video_title;
    }

    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
