package com.happi.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CategoryWiseShowsModel implements Serializable {


    @SerializedName("category_id")
    @Expose
    int category_id;
    @SerializedName("category_name")
    @Expose
    String category_name;
    @SerializedName("videos")
    @Expose
    List<ShowModel> videos;


    public int getCategory_id() {
        return category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public List<ShowModel> getVideos() {
        return videos;
    }
}
