package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class YTModel implements Serializable {


    @SerializedName("name")
    private String name= "";
    @SerializedName("description")
    private String description= "";
    @SerializedName("channel_name")
    private String channel_name= "";
    @SerializedName("key")
    private String key= "";
    @SerializedName("thumbnail")
    private String thumbnail= "";


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }


}
