package com.happi.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoriesHomeListVideoModel {

    @SerializedName("id")
    private Integer categoryId;
    @SerializedName("title")
    private String categoryName;
    @SerializedName("data")
   // private List<VideoModel> videoModelList;
    private List<ShowModel> videoModelList;
    @SerializedName("premium_flag")
    private Integer premium_flag;

    public Integer getPremium_flag() {
        return premium_flag;
    }

    public void setPremium_flag(Integer premium_flag) {
        this.premium_flag = premium_flag;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

   // public List<VideoModel> getVideoModelList() {
    public List<ShowModel> getVideoModelList() {
        return videoModelList;
    }

   // public void setVideoModelList(List<VideoModel> videoModelList) {
    public void setVideoModelList(List<ShowModel> videoModelList) {
        this.videoModelList = videoModelList;
    }
}